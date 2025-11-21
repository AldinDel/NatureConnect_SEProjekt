const numParticipantsInput = document.getElementById("numParticipants");
const participantsContainer = document.getElementById("participantsContainer");

const discountInput = document.getElementById("discountCode");
const discountBtn = document.getElementById("applyDiscountBtn");
const discountMessage = document.getElementById("discountMessage");
const removeDiscountBtn = document.getElementById("removeDiscountBtn");
const voucherCodeField = document.getElementById("voucherCodeField");

removeDiscountBtn.addEventListener("click", () => {
    currentDiscount = 0;

    discountInput.readOnly = false;
    discountInput.value = "";
    voucherCodeField.value = null;

    discountBtn.disabled = false;
    discountBtn.classList.remove("btn-disabled");

    discountMessage.classList.add("hidden");
    removeDiscountBtn.classList.add("hidden");

    updatePriceSummary();
});


const BASE_PRICE = parseFloat(document.getElementById("eventBasePrice").value) || 0;
const MIN_PARTICIPANTS = parseInt(document.getElementById("eventMinParticipants").value) || 1;
const MAX_PARTICIPANTS = parseInt(document.getElementById("eventMaxParticipants").value) || 20;
let currentDiscount = 0;

function clamp(n, min, max) {
    return Math.max(min, Math.min(max, n));
}

function generateParticipants(num) {
    participantsContainer.innerHTML = "";

    for (let i = 0; i < num; i++) {
        const section = document.createElement("div");
        section.classList.add("participant-section");

        section.innerHTML = `
            <h3 class="participant-title">Participant ${i + 1}</h3>
            <div class="form-group">
                <label>First Name</label>
                <input class="form-input" type="text" name="participants[${i}].firstName" maxlength="50"/>
            </div>
            <div class="form-group">
                <label>Last Name</label>
                <input class="form-input" type="text" name="participants[${i}].lastName" maxlength="50"/>
            </div>
            <div class="form-group">
                <label>Age</label>
                <input class="form-input" type="number" name="participants[${i}].age" min="1" max="120"/>
            </div>
        `;

        const ageInput = section.querySelector(`input[name="participants[${i}].age"]`);

        ageInput.addEventListener("input", () => {
            let value = parseInt(ageInput.value, 10);

            if (value > 120) {
                ageInput.value = 120;
            }
            if (value < 1) {
                ageInput.value = 1;
            }
        });

        participantsContainer.appendChild(section);
        participantsContainer.appendChild(section);
    }
}

function syncSeatsFormField(value) {
    const seatsField = document.querySelector('[name="seats"]');
    if (seatsField) seatsField.value = value;
}

async function loadEquipmentForEvent(eventId) {
    try {
        const res = await fetch(`/api/events/${eventId}/equipment`);
        if (!res.ok) throw new Error("Failed to load equipment");

        const items = await res.json();
        const container = document.getElementById("equipmentContainer");
        container.innerHTML = "";

        items.forEach(item => {

            const wrapper = document.createElement("label");
            wrapper.className = "checkbox-label";

            const cb = document.createElement("input");
            cb.type = "checkbox";
            cb.name = `equipment[${item.id}].selected`;
            cb.dataset.price = item.unitPrice;
            cb.value = "true";

            const content = document.createElement("div");
            content.className = "addon-content";

            const name = document.createElement("p");
            name.className = "addon-name";
            name.textContent = item.name;

            const stockInfo = document.createElement("p");
            stockInfo.className = "addon-desc";
            stockInfo.textContent = `Available: ${item.stock}`;

            content.appendChild(name);
            content.appendChild(stockInfo);

            const price = document.createElement("p");
            price.className = "addon-price";
            price.textContent = `${item.unitPrice.toFixed(2)} €`;

            const qty = document.createElement("input");
            qty.type = "number";
            qty.min = 1;
            qty.max = item.stock;
            qty.value = 1;
            qty.name = `equipment[${item.id}].quantity`;
            qty.className = "form-input";
            qty.style = "width: 70px; display: none;";

            cb.addEventListener("change", () => {
                qty.style.display = cb.checked ? "block" : "none";
                updatePriceSummary();
            });

            qty.addEventListener("input", () => {
                const participants = parseInt(numParticipantsInput.value, 10) || 1;

                if (qty.value < 1) qty.value = 1;
                if (qty.value > item.stock) qty.value = item.stock;
                if (qty.value > participants) qty.value = participants;

                updatePriceSummary();
            });


            wrapper.appendChild(cb);
            wrapper.appendChild(content);
            wrapper.appendChild(price);
            wrapper.appendChild(qty);

            container.appendChild(wrapper);
        });

        updatePriceSummary();
    } catch (err) {
        console.error(err);
    }
}

function updatePriceSummary() {
    const participants = parseInt(numParticipantsInput.value, 10) || 1;
    const participantsCost = BASE_PRICE * participants;

    let addonsCost = 0;
    const equipmentSummary = document.getElementById("summaryEquipment");
    equipmentSummary.innerHTML = "";

    document.querySelectorAll('input[type="checkbox"][name^="equipment"]').forEach(cb => {
        if (!cb.checked) return;

        const wrapper = cb.closest(".checkbox-label");
        const qtyInput = wrapper.querySelector('input[type="number"]');

        const qty = qtyInput ? parseInt(qtyInput.value, 10) || 1 : 1;
        const price = parseFloat(cb.dataset.price) || 0;
        const totalForItem = qty * price;
        addonsCost += qty * price;
        const name = wrapper.querySelector(".addon-name").textContent;
        const line = document.createElement("div");
        line.className = "price-row";
        line.innerHTML = `
            <span>${name} × ${qty}</span>
            <span>${totalForItem.toFixed(2)} €</span>
        `;
        equipmentSummary.appendChild(line);
    });


    const subtotal = participantsCost + addonsCost;
    const discountAmount = subtotal * (currentDiscount / 100);
    const total = subtotal - discountAmount;

    document.getElementById("summarySeats").textContent = participants;
    document.getElementById("summaryTotal").textContent = `${total.toFixed(2)} €`;
}

if (discountBtn) {
    discountBtn.addEventListener("click", async () => {
        const code = discountInput.value.trim();

        if (!code) return;

        const res = await fetch(`/api/vouchers/validate?code=` + code);
        const data = await res.json();
        if (data.valid) {
            currentDiscount = data.discountPercent;
            discountMessage.classList.remove("text-red");
            discountMessage.classList.add("text-green");
            discountMessage.textContent = "✓ Voucher valid";

            voucherCodeField.value = code;

            discountInput.readOnly = true;
            discountBtn.disabled = true;
            discountBtn.classList.add("btn-disabled");

            removeDiscountBtn.classList.remove("hidden");

        } else {
            currentDiscount = 0;
            discountMessage.classList.remove("text-green");
            discountMessage.classList.add("text-red");
            discountMessage.textContent = "✕ Invalid code";

            voucherCodeField.value = null;
        }

        discountMessage.classList.remove("hidden");
        updatePriceSummary();

    });
}

if (numParticipantsInput) {
    numParticipantsInput.addEventListener("input", () => {
        let value = parseInt(numParticipantsInput.value, 10) || 1;
        value = clamp(value, 1, MAX_PARTICIPANTS);

        numParticipantsInput.value = value;
        syncSeatsFormField(value);
        generateParticipants(value);
        document.querySelectorAll('input[type="number"][name^="equipment"]').forEach(qty => {
            const maxStock = parseInt(qty.max, 10);

            if (qty.value > value) qty.value = value;
            if (qty.value > maxStock) qty.value = maxStock;
        });

        updatePriceSummary();
    });
}

document.addEventListener("DOMContentLoaded", () => {
    function showError(message) {
        console.log("showError called with:", message); // DEBUG

        const box = document.getElementById("errorPop");

        const error = document.createElement("div");
        error.className = "error";
        error.textContent = message;

        box.appendChild(error);

        setTimeout(() => {
            error.remove();
        }, 2000);
    }

    const eventIdEl = document.getElementById("pageEventId");

    if (!eventIdEl) {
        console.error("❌ Missing hidden eventId!");
        return;
    }

    const eventId = eventIdEl.value;

    if (!eventId) {
        console.error("❌ eventId is empty!");
        return;
    }

    const form = document.querySelector("form");
    const errorBox = document.getElementById("errorBox");
    const confirmBtn = document.getElementById("confirmBtn");

    form.addEventListener("submit", async (event) => {
        event.preventDefault();

        errorBox.classList.add("hidden");
        errorBox.innerHTML = "";
        confirmBtn.disabled = true;
        confirmBtn.textContent = "Validating...";

        const formData = new FormData(form);
        const json = {};
        const equipmentData = {};

        formData.forEach((value, key) => {
            json[key] = value;
        });

        Object.keys(json).forEach(key => {
            const match = key.match(/^equipment\[(\d+)]\.(\w+)$/);
            if (match) {
                const id = parseInt(match[1]);
                const field = match[2];

                if (!equipmentData[id]) {
                    equipmentData[id] = {};
                }

                equipmentData[id][field] = json[key];
                delete json[key];
            }
        });

        json.equipment = equipmentData;
        json.discountPercent = currentDiscount;

        const participants = [];

        Object.keys(json).forEach(key => {
            const match = key.match(/^participants\[(\d+)]\.(\w+)$/);
            if (match) {
                const index = parseInt(match[1]);
                const field = match[2];

                if (!participants[index]) {
                    participants[index] = {};
                }

                participants[index][field] = json[key];

                delete json[key];
            }
        });

        json.participants = participants;
        const res = await fetch("/api/bookings", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(json)
        });

        if (!res.ok) {
            const body = await res.json();

            if (Array.isArray(body)) {
                body.forEach(msg => showError(msg));
            } else if (body.message) {
                body.message.split("|").forEach(msg => showError(msg.trim()));
            }
            confirmBtn.disabled = false;
            confirmBtn.textContent = "Confirm Booking";
            return;
        }
        const booking = await res.json();
        window.location.href = "/booking/payment/" + booking.id;
    });

    generateParticipants(1);
    numParticipantsInput.value = 1;
    syncSeatsFormField(1);
    loadEquipmentForEvent(eventId);
    updatePriceSummary();
});