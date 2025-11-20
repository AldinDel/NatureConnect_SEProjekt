/* -------------------------------------------
   ELEMENT REFERENCES
--------------------------------------------*/
const numParticipantsInput = document.getElementById("numParticipants");
const participantsContainer = document.getElementById("participantsContainer");

const discountInput = document.getElementById("discountCode");
const discountBtn = document.getElementById("applyDiscountBtn");
const discountMessage = document.getElementById("discountMessage");

const BASE_PRICE = parseFloat(document.getElementById("eventBasePrice").value) || 0;
const MIN_PARTICIPANTS = parseInt(document.getElementById("eventMinParticipants").value) || 1;
const MAX_PARTICIPANTS = parseInt(document.getElementById("eventMaxParticipants").value) || 20;
let currentDiscount = 0;


/* -------------------------------------------
   UTILS
--------------------------------------------*/
function clamp(n, min, max) {
    return Math.max(min, Math.min(max, n));
}

/* -------------------------------------------
   PARTICIPANTS
--------------------------------------------*/
function generateParticipants(num) {
    participantsContainer.innerHTML = "";

    for (let i = 0; i < num; i++) {
        const section = document.createElement("div");
        section.classList.add("participant-section");

        section.innerHTML = `
            <h3 class="participant-title">Participant ${i + 1}</h3>
            <div class="form-group">
                <label>First Name</label>
                <input class="form-input" type="text" name="participants[${i}].firstName" />
            </div>
            <div class="form-group">
                <label>Last Name</label>
                <input class="form-input" type="text" name="participants[${i}].lastName" />
            </div>
            <div class="form-group">
                <label>Age</label>
                <input class="form-input" type="number" name="participants[${i}].age" min="1" />
            </div>
        `;

        participantsContainer.appendChild(section);
    }
}

function syncSeatsFormField(value) {
    const seatsField = document.querySelector('[name="seats"]');
    if (seatsField) seatsField.value = value;
}

/* -------------------------------------------
   LOAD EQUIPMENT (AJAX)
--------------------------------------------*/
async function loadEquipmentForEvent(eventId) {
    try {
        const res = await fetch(`/api/events/${eventId}/equipment`);
        if (!res.ok) throw new Error("Failed to load equipment");

        const items = await res.json();
        const container = document.getElementById("equipmentContainer");
        container.innerHTML = "";

        items.forEach(item => {
            const wrapper = document.createElement("div");
            wrapper.className = "checkbox-label";

            // Checkbox
            const cb = document.createElement("input");
            cb.type = "checkbox";
            cb.name = `equipment[${item.id}].selected`;
            cb.dataset.price = item.unitPrice;
            cb.value = "true";

            // Content
            const content = document.createElement("div");
            content.className = "addon-content";

            const title = document.createElement("p");
            title.className = "addon-name";
            title.textContent = item.name;

            const price = document.createElement("p");
            price.className = "addon-price";
            price.textContent = `${item.unitPrice.toFixed(2)} €`;

            content.appendChild(title);
            content.appendChild(price);

            // Quantity input
            const qty = document.createElement("input");
            qty.type = "number";
            qty.min = 1;
            qty.value = 1;
            qty.name = `equipment[${item.id}].quantity`;
            qty.className = "form-input";
            qty.style = "width: 80px; display:none;"; // HIDDEN until checkbox ticked

            wrapper.appendChild(cb);
            wrapper.appendChild(content);
            wrapper.appendChild(qty);

            container.appendChild(wrapper);

            // Show quantity only when checked
            cb.addEventListener("change", () => {
                qty.style.display = cb.checked ? "block" : "none";
                updatePriceSummary();
            });

            qty.addEventListener("input", updatePriceSummary);
        });

        updatePriceSummary();
    } catch (err) {
        console.error(err);
    }
}


/* -------------------------------------------
   PRICE SUMMARY
--------------------------------------------*/
function updatePriceSummary() {
    const participants = parseInt(numParticipantsInput.value, 10) || 1;
    const participantsCost = BASE_PRICE * participants;

    let addonsCost = 0;

    document.querySelectorAll('input[type="checkbox"][name^="equipment"]').forEach(cb => {
        if (!cb.checked) return;

        const wrapper = cb.closest(".checkbox-label");
        const qtyInput = wrapper.querySelector('input[type="number"]');

        const qty = qtyInput ? parseInt(qtyInput.value, 10) || 1 : 1;
        const price = parseFloat(cb.dataset.price) || 0;

        addonsCost += qty * price;
    });


    const subtotal = participantsCost + addonsCost;
    const discountAmount = subtotal * (currentDiscount / 100);
    const total = subtotal - discountAmount;

    document.getElementById("summarySeats").textContent = participants;
    document.getElementById("summaryTotal").textContent = `${total.toFixed(2)} €`;
}

/* -------------------------------------------
   DISCOUNT
--------------------------------------------*/
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

            discountMessage.textContent = "✓ " + data.message;
            voucherCodeField.value = code;

        } else {
            currentDiscount = 0;
            discountMessage.classList.remove("text-green");
            discountMessage.classList.add("text-red");

            discountMessage.textContent = "✕ " + data.message;
            voucherCodeField.value = null;
        }

        discountMessage.classList.remove("hidden");
        updatePriceSummary();

    });
}


/* -------------------------------------------
   PARTICIPANT HANDLING
--------------------------------------------*/
if (numParticipantsInput) {
    numParticipantsInput.addEventListener("input", () => {
        let value = parseInt(numParticipantsInput.value, 10) || 1;
        value = clamp(value, 1, MAX_PARTICIPANTS);

        numParticipantsInput.value = value;
        syncSeatsFormField(value);
        generateParticipants(value);
        updatePriceSummary();
    });
}

async function loadPaymentMethods() {
    const select = document.getElementById("paymentMethod");

    const res = await fetch("/api/bookings/payment-methods");
    const methods = await res.json();

    select.innerHTML = `<option value="" disabled selected>Select a payment method</option>`;

    methods.forEach(m => {
        const opt = document.createElement("option");
        opt.value = m;
        opt.textContent = m.replace("_", " ").toUpperCase();
        select.appendChild(opt);
    });
}


/* -------------------------------------------
   INITIAL LOAD
--------------------------------------------*/
document.addEventListener("DOMContentLoaded", () => {

    // --- Error Popup ---
    function showError(message) {
        console.log("showError called with:", message); // DEBUG

        const box = document.getElementById("errorPop");

        const error = document.createElement("div");
        error.className = "error";
        error.textContent = message;

        box.appendChild(error);

        setTimeout(() => {
            error.remove();
        }, 5000);
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

        formData.forEach((value, key) => {
            json[key] = value;
        });

        const res = await fetch("/api/bookings", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(json)
        });

        if (!res.ok) {
            const errors = await res.json();
            errors.forEach(msg => showError(msg));
            confirmBtn.disabled = false;
            confirmBtn.textContent = "Confirm Booking";
            return;
        }

        const booking = await res.json();
        window.location.href = "/booking/confirmation/" + booking.id;
    });

    // Initial load
    generateParticipants(1);
    numParticipantsInput.value = 1;
    syncSeatsFormField(1);
    loadEquipmentForEvent(eventId);
    loadPaymentMethods();
    updatePriceSummary();
});