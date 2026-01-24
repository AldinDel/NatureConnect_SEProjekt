import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import bookingService from '../../services/bookingService';
import './BookingForm.css';

const BookingForm = ({ event, onClose }) => {
  const navigate = useNavigate();
  const [paymentMethods, setPaymentMethods] = useState([]);
  const [formData, setFormData] = useState({
    eventId: event.id,
    eventDate: event.date,
    bookerFirstName: '',
    bookerLastName: '',
    bookerEmail: '',
    seats: 1,
    audience: 'INDIVIDUAL',
    voucherCode: '',
    specialNotes: '',
    paymentMethod: 'CREDIT_CARD',
    participants: [],
    equipment: {},
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  useEffect(() => {
    loadPaymentMethods();
  }, []);

  const loadPaymentMethods = async () => {
    try {
      const methods = await bookingService.getPaymentMethods();
      setPaymentMethods(methods);
      if (methods.length > 0) {
        setFormData(prev => ({ ...prev, paymentMethod: methods[0] }));
      }
    } catch (err) {
      console.error('Error loading payment methods:', err);
    }
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: name === 'seats' ? parseInt(value) : value
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError(null);

    try {
      const booking = await bookingService.createBooking(formData);

      // Redirect to payment or confirmation page
      alert(`Booking successful! Booking ID: ${booking.id}`);
      onClose();

      // Optional: Navigate to a confirmation page
      // navigate(`/bookings/${booking.id}/confirmation`);
    } catch (err) {
      setError(err.message || 'Failed to create booking');
      console.error('Booking error:', err);
    } finally {
      setLoading(false);
    }
  };

  const totalPrice = event.price * formData.seats;

  return (
      <div className="booking-modal-overlay" onClick={onClose}>
        <div className="booking-modal" onClick={(e) => e.stopPropagation()}>
          <div className="booking-header">
            <h2>Book: {event.title}</h2>
            <button className="close-btn" onClick={onClose}>✕</button>
          </div>

          <form onSubmit={handleSubmit} className="booking-form">
            {error && (
                <div className="booking-error">
                  {error}
                </div>
            )}

            <div className="form-section">
              <h3>Your Information</h3>

              <div className="form-row">
                <div className="form-group">
                  <label>First Name *</label>
                  <input
                      type="text"
                      name="bookerFirstName"
                      value={formData.bookerFirstName}
                      onChange={handleChange}
                      required
                  />
                </div>

                <div className="form-group">
                  <label>Last Name *</label>
                  <input
                      type="text"
                      name="bookerLastName"
                      value={formData.bookerLastName}
                      onChange={handleChange}
                      required
                  />
                </div>
              </div>

              <div className="form-group">
                <label>Email *</label>
                <input
                    type="email"
                    name="bookerEmail"
                    value={formData.bookerEmail}
                    onChange={handleChange}
                    required
                />
              </div>
            </div>

            <div className="form-section">
              <h3>Booking Details</h3>

              <div className="form-row">
                <div className="form-group">
                  <label>Number of Seats *</label>
                  <input
                      type="number"
                      name="seats"
                      value={formData.seats}
                      onChange={handleChange}
                      min="1"
                      max={event.maxParticipants}
                      required
                  />
                </div>

                <div className="form-group">
                  <label>Audience Type</label>
                  <select
                      name="audience"
                      value={formData.audience}
                      onChange={handleChange}
                  >
                    <option value="INDIVIDUAL">Individual</option>
                    <option value="GROUP">Group</option>
                  </select>
                </div>
              </div>

              <div className="form-group">
                <label>Voucher Code (optional)</label>
                <input
                    type="text"
                    name="voucherCode"
                    value={formData.voucherCode}
                    onChange={handleChange}
                    placeholder="Enter voucher code"
                />
              </div>

              <div className="form-group">
                <label>Special Notes (optional)</label>
                <textarea
                    name="specialNotes"
                    value={formData.specialNotes}
                    onChange={handleChange}
                    placeholder="Any special requests or notes..."
                    rows="3"
                />
              </div>
            </div>

            <div className="form-section">
              <h3>Payment Method</h3>

              <div className="form-group">
                <select
                    name="paymentMethod"
                    value={formData.paymentMethod}
                    onChange={handleChange}
                    required
                >
                  {paymentMethods.map(method => (
                      <option key={method} value={method}>
                        {method.replace('_', ' ')}
                      </option>
                  ))}
                </select>
              </div>
            </div>

            <div className="booking-summary">
              <div className="summary-row">
                <span>Price per person:</span>
                <span>€ {event.price}</span>
              </div>
              <div className="summary-row">
                <span>Number of seats:</span>
                <span>{formData.seats}</span>
              </div>
              <div className="summary-row total">
                <span>Total:</span>
                <span>€ {totalPrice.toFixed(2)}</span>
              </div>
            </div>

            <div className="booking-actions">
              <button
                  type="button"
                  className="btn-cancel"
                  onClick={onClose}
                  disabled={loading}
              >
                Cancel
              </button>
              <button
                  type="submit"
                  className="btn-book"
                  disabled={loading}
              >
                {loading ? 'Processing...' : 'Complete Booking'}
              </button>
            </div>
          </form>
        </div>
      </div>
  );
};

export default BookingForm;
