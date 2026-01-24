import { useState } from 'react';
import eventService from '../../services/eventService';
import './BookingButton.css';

const BookingButton = ({ event }) => {
  const [showModal, setShowModal] = useState(false);
  const [loading, setLoading] = useState(false);
  const [success, setSuccess] = useState(false);
  const [error, setError] = useState(null);
  const [formData, setFormData] = useState({
    bookerFirstName: '',
    bookerLastName: '',
    bookerEmail: '',
    seats: 1,
    specialNotes: ''
  });

  const handleInputChange = (e) => {
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
      const bookingData = {
        eventId: event.id,
        eventDate: event.date,
        bookerFirstName: formData.bookerFirstName,
        bookerLastName: formData.bookerLastName,
        bookerEmail: formData.bookerEmail,
        seats: formData.seats,
        audience: event.audience || 'ADULTS',
        specialNotes: formData.specialNotes,
        paymentMethod: 'INVOICE',
        participants: []
      };

      await eventService.createBooking(bookingData);
      setSuccess(true);
      setTimeout(() => {
        setShowModal(false);
        setSuccess(false);
        setFormData({
          bookerFirstName: '',
          bookerLastName: '',
          bookerEmail: '',
          seats: 1,
          specialNotes: ''
        });
      }, 2000);
    } catch (err) {
      setError('Buchung fehlgeschlagen. Bitte versuchen Sie es erneut.');
      console.error('Booking error:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleOpenModal = () => {
    setShowModal(true);
    setError(null);
    setSuccess(false);
  };

  const handleCloseModal = () => {
    setShowModal(false);
    setError(null);
    setSuccess(false);
  };

  if (event.cancelled) {
    return (
      <button className="booking-button disabled" disabled>
        Event abgesagt
      </button>
    );
  }

  return (
    <>
      <button className="booking-button" onClick={handleOpenModal}>
        Jetzt buchen
      </button>

      {showModal && (
        <div className="booking-modal-overlay" onClick={handleCloseModal}>
          <div className="booking-modal" onClick={(e) => e.stopPropagation()}>
            <div className="booking-modal-header">
              <h2>Event buchen</h2>
              <button
                className="close-button"
                onClick={handleCloseModal}
                aria-label="Close"
              >
                &times;
              </button>
            </div>

            {success ? (
              <div className="booking-success">
                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  width="64"
                  height="64"
                  viewBox="0 0 24 24"
                  fill="none"
                  stroke="currentColor"
                  strokeWidth="2"
                  strokeLinecap="round"
                  strokeLinejoin="round"
                >
                  <polyline points="20 6 9 17 4 12"></polyline>
                </svg>
                <h3>Buchung erfolgreich!</h3>
                <p>Sie erhalten eine Bestätigung per E-Mail.</p>
              </div>
            ) : (
              <form onSubmit={handleSubmit} className="booking-form">
                <div className="form-group">
                  <label htmlFor="bookerFirstName">Vorname *</label>
                  <input
                    type="text"
                    id="bookerFirstName"
                    name="bookerFirstName"
                    value={formData.bookerFirstName}
                    onChange={handleInputChange}
                    required
                  />
                </div>

                <div className="form-group">
                  <label htmlFor="bookerLastName">Nachname *</label>
                  <input
                    type="text"
                    id="bookerLastName"
                    name="bookerLastName"
                    value={formData.bookerLastName}
                    onChange={handleInputChange}
                    required
                  />
                </div>

                <div className="form-group">
                  <label htmlFor="bookerEmail">E-Mail *</label>
                  <input
                    type="email"
                    id="bookerEmail"
                    name="bookerEmail"
                    value={formData.bookerEmail}
                    onChange={handleInputChange}
                    required
                  />
                </div>

                <div className="form-group">
                  <label htmlFor="seats">Anzahl Plätze *</label>
                  <input
                    type="number"
                    id="seats"
                    name="seats"
                    min="1"
                    max={event.maxParticipants || 50}
                    value={formData.seats}
                    onChange={handleInputChange}
                    required
                  />
                </div>

                <div className="form-group">
                  <label htmlFor="specialNotes">Besondere Hinweise</label>
                  <textarea
                    id="specialNotes"
                    name="specialNotes"
                    value={formData.specialNotes}
                    onChange={handleInputChange}
                    rows="3"
                  />
                </div>

                {error && <div className="booking-error">{error}</div>}

                <div className="booking-summary">
                  <p><strong>Event:</strong> {event.title}</p>
                  <p><strong>Datum:</strong> {new Date(event.date).toLocaleDateString('de-DE')}</p>
                  {event.price && (
                    <p className="booking-price">
                      <strong>Gesamtpreis:</strong> €{(event.price * formData.seats).toFixed(2)}
                    </p>
                  )}
                </div>

                <div className="booking-actions">
                  <button
                    type="button"
                    className="button-secondary"
                    onClick={handleCloseModal}
                  >
                    Abbrechen
                  </button>
                  <button
                    type="submit"
                    className="button-primary"
                    disabled={loading}
                  >
                    {loading ? 'Wird gebucht...' : 'Buchung abschließen'}
                  </button>
                </div>
              </form>
            )}
          </div>
        </div>
      )}
    </>
  );
};

export default BookingButton;
