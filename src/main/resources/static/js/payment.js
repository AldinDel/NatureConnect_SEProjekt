function selectPayment(method) {
    // Remove selections
    document.querySelectorAll('.payment-option').forEach(opt => {
        opt.classList.remove('selected');
    });

    // Hide details
    document.querySelectorAll('.payment-details').forEach(detail => {
        detail.classList.remove('active');
    });

    const radioId =
        method === 'CREDIT_CARD' ? 'creditCard' :
            method === 'PAYPAL' ? 'paypal' :
                method === 'INVOICE' ? 'invoice' :
                    'onSite';

    document.getElementById(radioId).checked = true;
    event.currentTarget.classList.add('selected');

    const detailsId =
        method === 'CREDIT_CARD' ? 'cardDetails' :
            method === 'PAYPAL' ? 'paypalDetails' :
                method === 'INVOICE' ? 'invoiceDetails' :
                    'onSiteDetails';

    document.getElementById(detailsId).classList.add('active');
}

// Format card number
document.getElementById('cardNumber')?.addEventListener('input', function(e) {
    let value = e.target.value.replace(/\s/g, '');
    let formattedValue = value.match(/.{1,4}/g)?.join(' ') || value;
    e.target.value = formattedValue;
});

// Expiry date format
document.getElementById('cardExpiry')?.addEventListener('input', function(e) {
    let value = e.target.value.replace(/\D/g, '');
    if (value.length >= 2) {
        value = value.slice(0, 2) + '/' + value.slice(2, 4);
    }
    e.target.value = value;
});

// CVC only numbers
document.getElementById('cardCvc')?.addEventListener('input', function(e) {
    e.target.value = e.target.value.replace(/\D/g, '');
});
