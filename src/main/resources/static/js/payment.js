function selectPayment(method, el) {
    document.querySelectorAll('.payment-option').forEach(opt => {
        opt.classList.remove('selected');
    });

    document.querySelectorAll('.payment-details').forEach(detail => {
        detail.classList.remove('active');
    });

    const radioId =
        method === 'CREDIT_CARD' ? 'creditCard' :
            method === 'PAYPAL' ? 'paypal' :
                method === 'INVOICE' ? 'invoice' :
                    'onSite';

    document.getElementById(radioId).checked = true;
    el.classList.add('selected');

    const detailsId =
        method === 'CREDIT_CARD' ? 'cardDetails' :
            method === 'PAYPAL' ? 'paypalDetails' :
                method === 'INVOICE' ? 'invoiceDetails' :
                    'onSiteDetails';

    document.getElementById(detailsId).classList.add('active');
}

document.getElementById('cardNumber')?.addEventListener('input', function(e) {
    let value = e.target.value.replace(/\s/g, '');
    let formattedValue = value.match(/.{1,4}/g)?.join(' ') || value;
    e.target.value = formattedValue;
});

document.getElementById('cardExpiry')?.addEventListener('input', function(e) {
    let value = e.target.value.replace(/\D/g, '');
    if (value.length >= 2) {
        value = value.slice(0, 2) + '/' + value.slice(2, 4);
    }
    e.target.value = value;
});

document.getElementById('cardCvc')?.addEventListener('input', function(e) {
    e.target.value = e.target.value.replace(/\D/g, '');
});

function cancelPayment() {
    window.history.back();
}

async function openPayment() {
    const bookingId = document.getElementById("bookingIdHidden").value;
    const amount = document.getElementById("amountHidden").value;
    const method = document.querySelector('input[name="paymentMethod"]:checked')?.value;

    if (!method) {
        alert("Please select a payment method.");
        return;
    }
    try {
        await fetch(`http://localhost:8080/booking/payment/${bookingId}`, {
            method: "POST",
            headers: {
                "Content-Type": "application/x-www-form-urlencoded"
            },
            body: `paymentMethod=${method}`
        });
    } catch (e) {
        alert("Payment method could not be saved.");
        return;
    }

    const token = Math.random().toString(36).substring(2);

    const paymentUrl =
        `http://localhost:9090/payment-service.html` +
        `?bookingId=${bookingId}` +
        `&amount=${amount}` +
        `&method=${method}` +
        `&token=${token}`;

    window.open(paymentUrl, "_blank");
}

