/* -------------------------------------------
   ACCOUNT BUTTONS
--------------------------------------------*/
const loginBtn = document.getElementById("loginBtn");
const guestBtn = document.getElementById("guestBtn");

if (loginBtn && guestBtn) {
    loginBtn.addEventListener("click", () => {
        loginBtn.classList.add("active");
        guestBtn.classList.remove("active");
    });

    guestBtn.addEventListener("click", () => {
        guestBtn.classList.add("active");
        loginBtn.classList.remove("active");
    });
}

/* -------------------------------------------
   RADIO BUTTON HANDLING
--------------------------------------------*/
document.querySelectorAll("[data-radio]").forEach(label => {
    const radio = label.querySelector("input");
    radio.addEventListener("change", () => {
        document.querySelectorAll("[data-radio]").forEach(l => l.classList.remove("active"));
        label.classList.add("active");
    });
});

/* -------------------------------------------
   CHECKBOX HANDLING
--------------------------------------------*/
document.querySelectorAll("[data-checkbox]").forEach(label => {
    const checkbox = label.querySelector("input");
    checkbox.addEventListener("change", () => {
        if (checkbox.checked) label.classList.add("active");
        else label.classList.remove("active");
        updatePriceSummary();
    });
});

/* -------------------------------------------
   PARTICIPANT GENERATION
--------------------------------------------*/
const numParticipantsInput = document.getElementById("numParticipants");
const participantsContainer = document.getElementById("participantsContainer");

function generateParticipants(num) {
    participantsContainer.innerHTML = "";

    for (let i = 0; i < num; i++) {
        const section = document.createElement("div");
        section.classList.add("participant-section");

        section.innerHTML = `
            <h3 class="participant-title">Participant ${i + 1}</h3>

            <div class="form-group">
                <label>First Name</label>
                <input class="form-input" type="text" name="participants[${i}].firstName">
            </div>

            <div class="form-group">
                <label>Last Name</label>
                <input class="form-input" type="text" name="participants[${i}].lastName">
            </div>

            <div class="form-group">
                <label>Age</label>
                <input class="form-input" type="number" name="participants[${i}].age" min="1">
            </div>
        `;

        participantsContainer.appendChild(section);
    }
}

numParticipantsInput.addEventListener("input", () => {
    let value = parseInt(numParticipantsInput.value) || 1;
    if (value < 1) value = 1;
    if (value > 20) value = 20;
    numParticipantsInput.value = value;

    generateParticipants(value);
    updatePriceSummary();
});

// Init
generateParticipants(1);

/* -------------------------------------------
   DISCOUNT
--------------------------------------------*/
let currentDiscount = 0;

const discountInput = document.getElementById("discountCode");
const discountBtn = document.getElementById("applyDiscountBtn");
const discountMessage = document.getElementById("discountMessage");

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

/* -------------------------------------------
   PRICE CALCULATION
--------------------------------------------*/
const BASE_PRICE = 75;

const ADDONS = [
    { id: 1, price: 50, perPerson: false },
    { id: 2, price: 8, perPerson: true }
];

function updatePriceSummary() {

    const participants = parseInt(numParticipantsInput.value) || 1;
    const participantsCost = BASE_PRICE * participants;

    let addonsCost = 0;
    document.querySelectorAll('input[name="addons"]:checked').forEach(cb => {
        const addon = ADDONS.find(a => a.id === parseInt(cb.value));
        if (addon) {
            addonsCost += addon.perPerson ? addon.price * participants : addon.price;
        }
    });

    const subtotal = participantsCost + addonsCost;
    const discountAmount = subtotal * (currentDiscount / 100);
    const total = subtotal - discountAmount;

    // Update DOM
    document.getElementById("summaryParticipants").textContent = participants;
    document.getElementById("summaryParticipantsCost").textContent = `$${participantsCost.toFixed(2)}`;
    document.getElementById("summarySubtotal").textContent = `$${subtotal.toFixed(2)}`;
    document.getElementById("summaryTotal").textContent = `$${total.toFixed(2)}`;

    // Addons
    if (addonsCost > 0) {
        document.getElementById("summaryAddonsRow").style.display = "flex";
        document.getElementById("summaryAddonsCost").textContent = `$${addonsCost.toFixed(2)}`;
    } else {
        document.getElementById("summaryAddonsRow").style.display = "none";
    }

    // Discount
    if (currentDiscount > 0) {
        document.getElementById("summaryDiscountRow").style.display = "flex";
        document.getElementById("summaryDiscountPercent").textContent = currentDiscount;
        document.getElementById("summaryDiscountAmount").textContent = `-$${discountAmount.toFixed(2)}`;
    } else {
        document.getElementById("summaryDiscountRow").style.display = "none";
    }
}

// Initial
updatePriceSummary();
