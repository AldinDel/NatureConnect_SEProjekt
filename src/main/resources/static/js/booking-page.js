const numParticipantsInput = document.getElementById("numParticipants");
const participantsContainer = document.getElementById("participantsContainer");

const isEditInput = document.getElementById("isEdit");
const isEditMode = isEditInput && isEditInput.value === "true";

let currentDiscount = 0;

const discountInput = document.getElementById("discountCode");
const discountBtn = document.getElementById("applyDiscountBtn");
const discountMessage = document.getElementById("discountMessage");
const removeDiscountBtn = document.getElementById("removeDiscountBtn");
const voucherCodeField = document.getElementById("voucherCodeField");
const discountRow = document.getElementById("summaryDiscountRow");
const discountText = document.getElementById("summaryDiscountText");
const discountAmount = document.getElementById("summaryDiscountAmount");

function showPopupError(message) {
    const popup = document.createElement("div");
    popup.className = "popup-message error";
    popup.textContent = message;

    document.body.appendChild(popup);

    setTimeout(() => {
        popup.classList.add("popup-hide");
        setTimeout(() => popup.remove(), 800);
    }, 2500);
}


if (removeDiscountBtn) {
    removeDiscountBtn.addEventListener("click", () => {
        currentDiscount = 0;
        discountInput.readOnly = false;
        discountInput.value = "";
        voucherCodeField.value = null;
        document.getElementById("discountPercentField").value = 0;
        discountBtn.disabled = false;
        discountBtn.classList.remove("btn-disabled");
        discountMessage.classList.add("hidden");
        removeDiscountBtn.classList.add("hidden");
        discountRow.style.display = "none";
        updatePriceSummary();
    });
}



const BASE_PRICE = parseFloat(document.getElementById("eventBasePrice").value) || 0;
const MIN_PARTICIPANTS = parseInt(document.getElementById("eventMinParticipants").value) || 1;
const MAX_PARTICIPANTS = parseInt(document.getElementById("eventMaxParticipants").value) || 20;

function clamp(n, min, max) {
    return Math.max(min, Math.min(max, n));
}

function generateParticipants(num) {
    participantsContainer.innerHTML = "";

    const existingDataContainer = document.getElementById("existingParticipantsData");
    const existing = existingDataContainer
        ? existingDataContainer.querySelectorAll("div[data-index]")
        : [];

    for (let i = 0; i < num; i++) {
        const section = document.createElement("div");
        section.classList.add("participant-section");

        section.innerHTML = `
        <h3 class="participant-title">Participant ${i + 1}</h3>
        <label>First Name</label>
        <input class="form-input participant-firstname" 
               name="participants[${i}].firstName" 
               data-participant-index="${i}"
               maxlength="50"
               pattern="[A-Za-zÄÖÜäöüß\\- ]+"
               title="Only letters, spaces, and hyphens are allowed"
               required>
        <span class="error-text participant-firstname-error" data-participant-index="${i}"></span>
    
        <label>Last Name</label>
        <input class="form-input participant-lastname" 
               name="participants[${i}].lastName" 
               data-participant-index="${i}"
               maxlength="50"
               pattern="[A-Za-zÄÖÜäöüß\\- ]+"
               title="Only letters, spaces, and hyphens are allowed"
               required>
        <span class="error-text participant-lastname-error" data-participant-index="${i}"></span>
    
        <label>Age</label>
        <input class="form-input participant-age" 
               type="number" 
               name="participants[${i}].age" 
               data-participant-index="${i}"
               min="1" 
               max="120">
        <span class="error-text participant-age-error" data-participant-index="${i}"></span>
    `;

        const row = Array.from(existing).find(el => Number(el.dataset.index) === i);
        if (row) {
            section.querySelector(`[name="participants[${i}].firstName"]`).value = row.dataset.firstname || "";
            section.querySelector(`[name="participants[${i}].lastName"]`).value = row.dataset.lastname || "";
            section.querySelector(`[name="participants[${i}].age"]`).value = row.dataset.age || "";
        }
        participantsContainer.appendChild(section);
    }
}

function displayFieldErrors(fieldErrors) {
    if (!fieldErrors) return;

    document.querySelectorAll('.input-error').forEach(el => el.classList.remove('input-error'));
    document.querySelectorAll('.error-text').forEach(el => {
        el.textContent = '';
        el.style.display = 'none';
    });

    for (const [field, message] of Object.entries(fieldErrors)) {
        if (field.startsWith('participants[')) {
            const match = field.match(/participants\[(\d+)\]\.(firstName|lastName|age)/);
            if (match) {
                const index = match[1];
                const fieldName = match[2];
                const input = document.querySelector(`[data-participant-index="${index}"].participant-${fieldName.toLowerCase()}`);
                const errorSpan = document.querySelector(`.participant-${fieldName.toLowerCase()}-error[data-participant-index="${index}"]`);

                if (input) input.classList.add('input-error');
                if (errorSpan) {
                    errorSpan.textContent = message;
                    errorSpan.style.display = 'block';
                }
            }
        }
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
            qty.className = "form-input";
            qty.style.cssText = "width: 70px; display: none;";
            qty.setAttribute('disabled', 'true');

            cb.addEventListener("change", () => {
                if (cb.checked) {
                    if (!qty.value || parseInt(qty.value) < 1) {
                        qty.value = 1;
                    }
                    qty.style.display = "block";
                    qty.removeAttribute('disabled');
                } else {
                    qty.value = 0;
                    qty.style.display = "none";
                    qty.setAttribute('disabled', 'true');
                }
                updatePriceSummary();
            });

            qty.addEventListener("input", () => {
                const participants = parseInt(numParticipantsInput.value, 10) || 1;

                if (qty.value < 1) qty.value = 1;
                if (qty.value > item.stock) qty.value = item.stock;

                if (qty.value > participants) {
                    qty.value = participants;
                    alert("You can't select more equipment than participants.");
                }

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



function updateDiscountRow(code, amount) {
    discountText.innerText = `Discount Code "${code}"`;
    discountAmount.innerText = `-${amount.toFixed(2)}€`;
    discountRow.classList.remove("hidden");
}

function hideDiscountRow() {
    discountRow.classList.add("hidden");
}

function initEquipmentListenersForEdit() {
    console.log("=== initEquipmentListenersForEdit START ===");
    console.log("Equipment containers found:", document.querySelectorAll('#equipmentContainer .checkbox-label').length);

    document.querySelectorAll('#equipmentContainer .checkbox-label').forEach(label => {
        const cb = label.querySelector('input[type="checkbox"][name^="equipment"]');
        const qty = label.querySelector('input[type="number"][name^="equipment"]');

        console.log("Checkbox:", cb?.name, "Checked:", cb?.checked, "Quantity:", qty?.value);
        if (!cb || !qty) return;

        if (cb.checked) {
            if (!qty.value || parseInt(qty.value) < 1) {
                qty.value = 1;
            }
            qty.style.display = "block";  // <- Diese Zeile ist wichtig!
        } else {
            qty.style.display = "none";
        }

        cb.addEventListener("change", () => {
            if (cb.checked) {
                qty.value = qty.value && parseInt(qty.value) > 0 ? qty.value : 1;
                qty.style.display = "block";
            } else {
                qty.style.display = "none";
            }
            updatePriceSummary();
        });

        qty.addEventListener("input", () => {
            const participants = parseInt(numParticipantsInput.value, 10) || 1;
            const maxStock = parseInt(qty.max, 10) || participants;

            if (qty.value < 1) qty.value = 1;
            if (qty.value > maxStock) qty.value = maxStock;

            if (qty.value > participants) {
                qty.value = participants;
                alert("You can't select more equipment than participants.");
            }

            updatePriceSummary();
        });
    });
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
        addonsCost += totalForItem;

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
    const discountValue = subtotal * (currentDiscount / 100);
    const total = subtotal - discountValue;

    document.getElementById("summaryParticipantsPrice").textContent = `${participants} × ${BASE_PRICE.toFixed(2)} €`;
    document.getElementById("summaryTotal").textContent = `${total.toFixed(2)} €`;


    if (currentDiscount > 0) {
        discountText.innerText = `Discount Code "${voucherCodeField.value}"`;
        discountAmount.innerText = `-${discountValue.toFixed(2)} €`;
        discountRow.style.display = "flex";
    } else {
        discountRow.style.display = "none";
    }
}


if (discountBtn) {
    discountBtn.addEventListener("click", async () => {
        const code = discountInput.value.trim();
        if (!code) return;

        const res = await fetch(`/api/vouchers/validate?code=` + code);
        const data = await res.json();

        if (data.valid) {
            currentDiscount = data.discountPercent;
            voucherCodeField.value = code;
            document.getElementById("discountPercentField").value = currentDiscount;

            discountMessage.classList.remove("text-red");
            discountMessage.classList.add("text-green");
            discountMessage.textContent = "✓ Voucher valid";

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

        document.querySelectorAll("#equipmentContainer input[type='number']").forEach(q => {
            if (parseInt(q.value) > value) {
                q.value = value;
            }
            q.max = value;
        });


        updatePriceSummary();
    });
}

document.addEventListener("DOMContentLoaded", () => {
    const popup = document.getElementById("error-popup");
    if (popup) {
        setTimeout(() => {
            popup.classList.add("popup-hide");
            setTimeout(() => popup.remove(), 800);
        }, 2000);
    }
});


document.addEventListener("DOMContentLoaded", () => {
    if (isEditMode) {
        const voucherDB = document.getElementById("voucherCodeField");
        const discountField = document.getElementById("discountPercentField");

        if (voucherDB && voucherDB.value) {
            discountInput.value = voucherDB.value;
            const discountPercent = parseFloat(discountField.value) || 0;

            if (discountPercent > 0) {
                currentDiscount = discountPercent;
                discountInput.readOnly = true;
                discountBtn.disabled = true;
                discountBtn.classList.add("btn-disabled");
                removeDiscountBtn.classList.remove("hidden");

                discountMessage.classList.add("text-green");
                discountMessage.textContent = "✓ Voucher applied";
                discountMessage.classList.remove("hidden");
            } else {
                discountMessage.textContent = "Voucher loaded";
                discountMessage.classList.remove("hidden");
            }
        }
    }

    updatePriceSummary();

    const eventIdEl = document.getElementById("pageEventId");
    if (!eventIdEl || !eventIdEl.value) {
        console.error("Missing eventId");
        return;
    }
    const eventId = eventIdEl.value;

    const currentIsEditMode = document.getElementById("isEdit")?.value === "true";
    console.log("Edit mode check:", currentIsEditMode);

    if (!currentIsEditMode) {
        generateParticipants(1);
        numParticipantsInput.value = 1;
        syncSeatsFormField(1);
        loadEquipmentForEvent(eventId);
    } else {
        const seatsField = document.querySelector('[name="seats"]');
        let seats = 1;
        if (seatsField && seatsField.value) seats = parseInt(seatsField.value, 10) || 1;
        numParticipantsInput.value = seats;
        syncSeatsFormField(seats);
        generateParticipants(seats);
        initEquipmentListenersForEdit();
    }

    const fieldErrorsElement = document.getElementById('fieldErrorsData');
    if (fieldErrorsElement) {
        try {
            const fieldErrors = JSON.parse(fieldErrorsElement.textContent);
            displayFieldErrors(fieldErrors);
        } catch (e) {
            console.error('Error parsing field errors:', e);
        }
    }


    updatePriceSummary();
});

