/* -------------------------------------------
   ELEMENT REFERENCES
--------------------------------------------*/
const numParticipantsInput = document.getElementById("numParticipants");
const participantsContainer = document.getElementById("participantsContainer");

const discountInput = document.getElementById("discountCode");
const discountBtn = document.getElementById("applyDiscountBtn");
const discountMessage = document.getElementById("discountMessage");

const BASE_PRICE = 75;
let currentDiscount = 0;

/* -------------------------------------------
   UTILS
--------------------------------------------*/
function clamp(n, min, max) {
    return Math.max(min, Math.min(max, n));
}

/* -------------------------------------------
   PARTICIPANTS GENERATION
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
   LOAD EQUIPMENT FOR EVENT (AJAX)
--------------------------------------------*/
async function loadEquipmentForEvent(eventId) {
    try {
        const res = await fetch(`/api/events/${eventId}/equipment`);
        if (!res.ok) throw new Error("Failed to load equipment");

        const items = await res.json();
        const container = document.getElementById("equipmentContainer");
        container.innerHTML = "";

        items.forEach(item => {
            const label = document.createElement("label");
            label.className = "checkbox-label";
            label.dataset.checkbox = "";

            const cb = document.createElement("input");
            cb.type = "checkbox";
            cb.name = `equipment[${item.id}]`;
            cb.value = item.id;
            cb.dataset.price = item.unitPrice;

            if (item.required) {
                cb.checked = true;
                cb.disabled = true;
            }

            const content = document.createElement("div");
            content.className = "addon-content";

            const title = document.createElement("p");
            title.className = "addon-name";
            title.textContent = item.name;

            const price = document.createElement("p");
            price.className = "addon-price";
            price.textContent = `${parseFloat(item.unitPrice).toFixed(2)} €`;

            content.appendChild(title);
            content.appendChild(price);

            label.appendChild(cb);
            label.appendChild(content);

            container.appendChild(label);

            cb.addEventListener("change", () => {
                updatePriceSummary();
            });
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

    // read dynamically loaded equipment
    document.querySelectorAll('input[name^="equipment["]').forEach(cb => {
        if (!cb.checked && !cb.disabled) return;

        const price = parseFloat(cb.dataset.price) || 0;
        addonsCost += price;
    });

    const subtotal = participantsCost + addonsCost;
    const discountAmount = subtotal * (currentDiscount / 100);
    const total = subtotal - discountAmount;

    document.getElementById("summarySeats").textContent = participants;
    document.getElementById("summaryTotal").textContent = `${total.toFixed(2)} €`;
}

/* -------------------------------------------
   DISCOUNT LOGIC
--------------------------------------------*/
if (discountBtn) {
    discountBtn.addEventListener("click", () => {
        const code = discountInput.value.trim().toUpperCase();

        if (code === "SUMMER20") {
            currentDiscount = 20;
            discountMessage.className = "text-green";
            discountMessage.innerHTML = "✓ Discount applied!";
        } else {
            currentDiscount = 0;
            discountMessage.className = "text-red";
            discountMessage.innerHTML = "✕ Invalid code";
        }

        discountMessage.classList.remove("hidden");
        updatePriceSummary();
    });
}

/* -------------------------------------------
   HANDLE PARTICIPANT INPUT CHANGE
--------------------------------------------*/
if (numParticipantsInput) {
    numParticipantsInput.addEventListener("input", () => {
        let value = parseInt(numParticipantsInput.value, 10) || 1;
        value = clamp(value, 1, 20);

        numParticipantsInput.value = value;
        syncSeatsFormField(value);
        generateParticipants(value);
        updatePriceSummary();
    });
}

/* -------------------------------------------
   INITIAL LOAD
--------------------------------------------*/
document.addEventListener("DOMContentLoaded", () => {
    // load participants
    const seatsField = document.querySelector('[name="seats"]');
    let initialSeats = seatsField && seatsField.value ? parseInt(seatsField.value, 10) : 1;

    numParticipantsInput.value = initialSeats;
    generateParticipants(initialSeats);

    syncSeatsFormField(initialSeats);

    // load event equipment
    const eventId = document.getElementById("pageEventId").value;
    loadEquipmentForEvent(eventId);

    updatePriceSummary();
});
