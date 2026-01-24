import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import bookingService from '../../services/bookingService';
import './PaymentPage.css';

const PaymentPage = () => {
  const { bookingId } = useParams();
  const navigate = useNavigate();
  const [booking, setBooking] = useState(null);
  const [paymentMethods, setPaymentMethods] = useState([]);
  const [selectedMethod, setSelectedMethod] = useState('CREDIT_CARD');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    loadBookingAndPaymentMethods();
  }, [bookingId]);

  const loadBookingAndPaymentMethods = async () => {
    try {
      setLoading(true);
      const [bookingData, methods] = await Promise.all([
        bookingService.getBookingById(bookingId),
        bookingService.getPaymentMethods()
      ]);
      setBooking(bookingData);
      setPaymentMethods(methods);
      if (bookingData.paymentMethod) {
        setSelectedMethod(bookingData.paymentMethod);
      }
      setError(null);
    } catch (err) {
      setError('Failed to load payment information');
      console.error('Error:', err);
    } finally {
      setLoading(false);
    }
  };

  const openPaymentService = async () => {
    if (!selectedMethod) {
      alert('Please select a payment method');
      return;
    }

    try {
      // Update payment method in backend
      await bookingService.updatePaymentMethod(bookingId, selectedMethod);

      // For ON_SITE payment, redirect directly to events page
      if (selectedMethod === 'ON_SITE') {
        navigate('/events');
        return;
      }

      // For other payment methods, open payment service in new window
      const token = Math.random().toString(36).substring(2);
      const host = window.location.hostname;
      const protocol = window.location.protocol;
      const paymentPort = '9090';
      const paymentBase = `${protocol}//${host}:${paymentPort}`;

      const paymentUrl =
          `${paymentBase}/payment-service.html` +
          `?bookingId=${bookingId}` +
          `&amount=${booking.totalPrice}` +
          `&method=${selectedMethod}` +
          `&token=${token}`;

      // Open payment service in new tab
      window.open(paymentUrl, '_blank');

      // Listen for payment completion (via webhook or polling)
      // For now, we'll redirect to events page after a delay
      setTimeout(() => {
        navigate('/events');
      }, 3000);

    } catch (err) {
      setError('Failed to process payment: ' + err.message);
      console.error('Payment error:', err);
    }
  };

  if (loading) {
    return (
        <div className="payment-page">
          <div className="loading">Loading payment information...</div>
        </div>
    );
  }

  if (error && !booking) {
    return (
        <div className="payment-page">
          <div className="error">{error}</div>
        </div>
    );
  }

  return (
      <div className="payment-page">
        <div className="payment-container">
          <div className="payment-header">
            <h1>Complete Your Payment</h1>
            <p>Booking ID: {bookingId}</p>
          </div>

          {error && <div className="alert-error">{error}</div>}

          <div className="payment-content">
            <div className="payment-method-section">
              <h3>Select Payment Method</h3>

              <div className="payment-methods">
                <div
                    className={`payment-option ${selectedMethod === 'CREDIT_CARD' ? 'selected' : ''}`}
                    onClick={() => setSelectedMethod('CREDIT_CARD')}
                >
                  <input
                      type="radio"
                      name="paymentMethod"
                      value="CREDIT_CARD"
                      checked={selectedMethod === 'CREDIT_CARD'}
                      onChange={() => setSelectedMethod('CREDIT_CARD')}
                  />
                  <div className="payment-info">
                    <strong>Credit Card</strong>
                    <span className="info-text">Pay securely with your card</span>
                  </div>
                </div>

                <div
                    className={`payment-option ${selectedMethod === 'PAYPAL' ? 'selected' : ''}`}
                    onClick={() => setSelectedMethod('PAYPAL')}
                >
                  <input
                      type="radio"
                      name="paymentMethod"
                      value="PAYPAL"
                      checked={selectedMethod === 'PAYPAL'}
                      onChange={() => setSelectedMethod('PAYPAL')}
                  />
                  <div className="payment-info">
                    <strong>PayPal</strong>
                    <span className="info-text">Fast and secure payment</span>
                  </div>
                </div>

                <div
                    className={`payment-option ${selectedMethod === 'INVOICE' ? 'selected' : ''}`}
                    onClick={() => setSelectedMethod('INVOICE')}
                >
                  <input
                      type="radio"
                      name="paymentMethod"
                      value="INVOICE"
                      checked={selectedMethod === 'INVOICE'}
                      onChange={() => setSelectedMethod('INVOICE')}
                  />
                  <div className="payment-info">
                    <strong>Invoice</strong>
                    <span className="info-text">Receive invoice via email</span>
                  </div>
                </div>

                <div
                    className={`payment-option ${selectedMethod === 'ON_SITE' ? 'selected' : ''}`}
                    onClick={() => setSelectedMethod('ON_SITE')}
                >
                  <input
                      type="radio"
                      name="paymentMethod"
                      value="ON_SITE"
                      checked={selectedMethod === 'ON_SITE'}
                      onChange={() => setSelectedMethod('ON_SITE')}
                  />
                  <div className="payment-info">
                    <strong>Pay On-Site</strong>
                    <span className="info-text">Pay at the venue</span>
                  </div>
                </div>
              </div>
            </div>

            <div className="payment-summary">
              <h3>Payment Summary</h3>
              <div className="summary-row">
                <span>Booking ID:</span>
                <span>{bookingId}</span>
              </div>
              <div className="summary-row total">
                <span>Total Amount:</span>
                <span>€ {booking?.totalPrice?.toFixed(2)}</span>
              </div>
            </div>

            <div className="payment-actions">
              <button
                  className="btn-cancel"
                  onClick={() => navigate('/events')}
              >
                Cancel
              </button>
              <button
                  className="btn-pay"
                  onClick={openPaymentService}
              >
                Complete Payment
              </button>
            </div>
          </div>
        </div>
      </div>
  );
};

export default PaymentPage;
