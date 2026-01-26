import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import eventService from '../../services/eventService';
import bookingService from '../../services/bookingService';
import './BookingPage.css';

const BookingPage = () => {
  const { eventId } = useParams();
  const navigate = useNavigate();
  const [event, setEvent] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [formData, setFormData] = useState({
    eventId: eventId,
    eventDate: '',
    bookerFirstName: '',
    bookerLastName: '',
    bookerEmail: '',
    bookerAddress: '',
    seats: 1,
    audience: 'INDIVIDUAL',
    voucherCode: '',
    specialNotes: '',
    paymentMethod: 'CREDIT_CARD',
    participants: [],
    equipment: {},
  });
  const [voucherInput, setVoucherInput] = useState('');
  const [discountPercent, setDiscountPercent] = useState(0);
  const [voucherMessage, setVoucherMessage] = useState('');

  useEffect(() => {
    loadEvent();
  }, [eventId]);

  useEffect(() => {
    // Initialize participants array when seats change
    const newParticipants = Array.from({ length: formData.seats }, (_, i) =>
      formData.participants[i] || { firstName: '', lastName: '', age: '' }
    );
    setFormData(prev => ({ ...prev, participants: newParticipants }));
  }, [formData.seats]);

  const loadEvent = async () => {
    try {
      setLoading(true);
      const data = await eventService.getEventById(eventId);
      setEvent(data);

      // Initialize equipment state
      const equipmentState = {};
      if (data.equipments) {
        data.equipments.forEach(eq => {
          equipmentState[eq.id] = { selected: false, quantity: 1 };
        });
      }

      // For non-recurring events, set date automatically if in the future
      if (!data.recurring) {
        const eventDate = new Date(data.date);
        const today = new Date();
        today.setHours(0, 0, 0, 0);

        if (eventDate >= today) {
          setFormData(prev => ({
            ...prev,
            eventDate: data.date,
            equipment: equipmentState
          }));
        }
      } else {
        setFormData(prev => ({ ...prev, equipment: equipmentState }));
      }

      setError(null);
    } catch (err) {
      setError('Failed to load event details');
      console.error('Error loading event:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: name === 'seats' ? parseInt(value) : value
    }));
  };

  const handleParticipantChange = (index, field, value) => {
    const newParticipants = [...formData.participants];
    newParticipants[index] = {
      ...newParticipants[index],
      [field]: value
    };
    setFormData(prev => ({ ...prev, participants: newParticipants }));
  };

  const handleEquipmentChange = (equipmentId, field, value) => {
    setFormData(prev => ({
      ...prev,
      equipment: {
        ...prev.equipment,
        [equipmentId]: {
          ...prev.equipment[equipmentId],
          [field]: field === 'quantity' ? parseInt(value) : value
        }
      }
    }));
  };

  const applyVoucher = () => {
    // Mock voucher validation - replace with actual API call
    if (voucherInput.toUpperCase() === 'DISCOUNT10') {
      setDiscountPercent(10);
      setFormData(prev => ({ ...prev, voucherCode: voucherInput }));
      setVoucherMessage('Voucher applied: 10% discount');
    } else if (voucherInput.toUpperCase() === 'SAVE20') {
      setDiscountPercent(20);
      setFormData(prev => ({ ...prev, voucherCode: voucherInput }));
      setVoucherMessage('Voucher applied: 20% discount');
    } else {
      setVoucherMessage('Invalid voucher code');
      setDiscountPercent(0);
    }
  };

  const removeVoucher = () => {
    setVoucherInput('');
    setDiscountPercent(0);
    setFormData(prev => ({ ...prev, voucherCode: '' }));
    setVoucherMessage('');
  };

  const calculateTotal = () => {
    if (!event) return 0;

    // Base price
    let total = event.price * formData.seats;

    // Add equipment costs
    if (event.equipments) {
      event.equipments.forEach(eq => {
        const selected = formData.equipment[eq.id];
        if (selected && selected.selected && eq.rentable) {
          total += eq.unitPrice * (selected.quantity || 1);
        }
      });
    }

    // Apply discount
    if (discountPercent > 0) {
      total = total * (1 - discountPercent / 100);
    }

    return total.toFixed(2);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError(null);

    try {
      const booking = await bookingService.createBooking({
        ...formData,
        discountPercent
      });
      // Redirect to payment page
      navigate(`/booking/payment/${booking.id}`);
    } catch (err) {
      setError(err.message || 'Failed to create booking');
      console.error('Booking error:', err);
      setLoading(false);
    }
  };

  const formatDate = (date) => {
    return new Date(date).toLocaleDateString('en-US', {
      month: 'long',
      day: 'numeric',
      year: 'numeric'
    });
  };

  const formatTime = (time) => {
    if (!time) return '';
    const [hours, minutes] = time;
    return `${String(hours).padStart(2, '0')}:${String(minutes).padStart(2, '0')}`;
  };

  if (loading && !event) {
    return (
      <div className="booking-page">
        <div className="loading">Loading...</div>
      </div>
    );
  }

  if (error && !event) {
    return (
      <div className="booking-page">
        <div className="error">{error}</div>
      </div>
    );
  }

  return (
    <>
      {/* Hero Section */}
      <div className="hero">
        <img src={event?.imageUrl || '/images/default_event.jpg'} alt="Event" />
        <div className="hero-overlay"></div>
      </div>

      <div className="container">
        {error && (
          <div className="alert-error">
            {error}
          </div>
        )}

        <form onSubmit={handleSubmit}>
          <div className="grid">
            {/* Left Column */}
            <div className="left-column">
              {/* Event Title */}
              <div className="card">
                <h1 className="title">{event?.title}</h1>

                {!event?.recurring && (
                  <p className="text-gray">
                    {formatDate(event?.date)} • {formatTime(event?.startTime)} - {formatTime(event?.endTime)}
                  </p>
                )}

                {event?.recurring && (
                  <p className="text-gray">
                    <strong>Recurring event</strong> • {formatTime(event?.startTime)} - {formatTime(event?.endTime)}
                  </p>
                )}
              </div>

              {/* Date Selection */}
              <div className="card">
                <h2 className="card-title">Date</h2>

                {!event?.recurring && (
                  <div>
                    <p className="text-gray">
                      <strong>{formatDate(event?.date)}</strong> • {formatTime(event?.startTime)} - {formatTime(event?.endTime)}
                    </p>
                  </div>
                )}

                {event?.recurring && (
                  <div>
                    <p className="text-gray">
                      Repeats from <strong>{formatDate(event?.recurrenceStart)}</strong> to <strong>{formatDate(event?.recurrenceEnd)}</strong>
                    </p>

                    <label style={{ marginTop: '1rem', display: 'block' }}>
                      Select date
                    </label>
                    <input
                      type="date"
                      className="form-input"
                      name="eventDate"
                      value={formData.eventDate}
                      onChange={handleChange}
                      min={event?.recurrenceStart}
                      max={event?.recurrenceEnd}
                      required
                    />
                  </div>
                )}
              </div>

              {/* Your Information */}
              <div className="card">
                <h2 className="card-title">Your Information</h2>

                <label>First Name</label>
                <input
                  type="text"
                  className="form-input"
                  name="bookerFirstName"
                  value={formData.bookerFirstName}
                  onChange={handleChange}
                  maxLength="50"
                  required
                />

                <label>Last Name</label>
                <input
                  type="text"
                  className="form-input"
                  name="bookerLastName"
                  value={formData.bookerLastName}
                  onChange={handleChange}
                  maxLength="50"
                  required
                />

                <label>Email</label>
                <input
                  type="email"
                  className="form-input"
                  name="bookerEmail"
                  value={formData.bookerEmail}
                  onChange={handleChange}
                  maxLength="100"
                  required
                />


              <label>Address</label>
              <input
                  type="text"
                  className="form-input"
                  name="bookerAddress"
                  value={formData.bookerAddress}
                  onChange={handleChange}
                  placeholder="Street, City, Postal Code"
                  maxLength="200"
                  required
              />
              </div>

              {/* Audience Type */}
              <div className="card">
                <h2 className="card-title">Audience Type</h2>

                <div className="radio-group">
                  <label className="radio-label">
                    <input
                      type="radio"
                      name="audience"
                      value="INDIVIDUAL"
                      checked={formData.audience === 'INDIVIDUAL'}
                      onChange={handleChange}
                    />
                    Individual
                  </label>

                  <label className="radio-label">
                    <input
                      type="radio"
                      name="audience"
                      value="GROUP"
                      checked={formData.audience === 'GROUP'}
                      onChange={handleChange}
                    />
                    Group
                  </label>

                  <label className="radio-label">
                    <input
                      type="radio"
                      name="audience"
                      value="COMPANY"
                      checked={formData.audience === 'COMPANY'}
                      onChange={handleChange}
                    />
                    Company
                  </label>
                </div>
              </div>

              {/* Participants */}
              <div className="card">
                <h2 className="card-title">Participants</h2>

                <label>Number of Participants</label>
                <select
                  className="form-input"
                  name="seats"
                  value={formData.seats}
                  onChange={handleChange}
                >
                  {Array.from({ length: event?.maxParticipants || 10 }, (_, i) => i + 1).map(num => (
                    <option key={num} value={num}>{num}</option>
                  ))}
                </select>

                <div className="participants-container">
                  {formData.participants.map((participant, index) => (
                    <div key={index} className="participant-item">
                      <h4>Participant {index + 1}</h4>
                      <div className="participant-fields">
                        <div className="form-group-inline">
                          <label>First Name</label>
                          <input
                            type="text"
                            className="form-input"
                            value={participant.firstName}
                            onChange={(e) => handleParticipantChange(index, 'firstName', e.target.value)}
                            required
                          />
                        </div>
                        <div className="form-group-inline">
                          <label>Last Name</label>
                          <input
                            type="text"
                            className="form-input"
                            value={participant.lastName}
                            onChange={(e) => handleParticipantChange(index, 'lastName', e.target.value)}
                            required
                          />
                        </div>
                        <div className="form-group-inline">
                          <label>Age</label>
                          <input
                            type="number"
                            className="form-input"
                            value={participant.age}
                            onChange={(e) => handleParticipantChange(index, 'age', e.target.value)}
                            min="1"
                            max="120"
                            required
                          />
                        </div>
                      </div>
                    </div>
                  ))}
                </div>
              </div>

              {/* Equipment & Add-ons */}
              {event?.equipments && event.equipments.length > 0 && (
                <div className="card">
                  <h2 className="card-title">Equipment & Add-ons</h2>

                  <div className="equipment-container">
                    {event.equipments.map(addon => (
                      <div key={addon.id} className="equipment-item">
                        <label className="checkbox-label">
                          <input
                            type="checkbox"
                            checked={formData.equipment[addon.id]?.selected || false}
                            onChange={(e) => handleEquipmentChange(addon.id, 'selected', e.target.checked)}
                          />

                          <div className="addon-content">
                            <p className="addon-name">{addon.name}</p>
                            <p className="addon-desc">Available: {addon.stock}</p>
                          </div>

                          <span className="addon-price">€{addon.unitPrice}</span>

                          {formData.equipment[addon.id]?.selected && (
                            <input
                              type="number"
                              className="equipment-quantity"
                              value={formData.equipment[addon.id]?.quantity || 1}
                              onChange={(e) => handleEquipmentChange(addon.id, 'quantity', e.target.value)}
                              min="1"
                              max={addon.stock}
                              onClick={(e) => e.stopPropagation()}
                            />
                          )}
                        </label>
                      </div>
                    ))}
                  </div>
                </div>
              )}

              {/* Special Notes */}
              <div className="card">
                <h2 className="card-title">Special Notes</h2>
                <textarea
                  className="form-input"
                  rows="4"
                  name="specialNotes"
                  value={formData.specialNotes}
                  onChange={handleChange}
                  maxLength="250"
                  style={{ resize: 'none' }}
                />
              </div>
            </div>

            {/* Right Column - Sidebar */}
            <div className="right-column">
              <div className="card sidebar">
                <h3 className="card-title">Summary</h3>

                <div className="price-row">
                  <span>Participants</span>
                  <span>{formData.seats} × €{event?.price}</span>
                </div>

                {/* Equipment Summary */}
                {event?.equipments && event.equipments.map(eq => {
                  const selected = formData.equipment[eq.id];
                  if (selected && selected.selected && eq.rentable) {
                    return (
                      <div key={eq.id} className="price-row">
                        <span>{eq.name} ×{selected.quantity || 1}</span>
                        <span>€{(eq.unitPrice * (selected.quantity || 1)).toFixed(2)}</span>
                      </div>
                    );
                  }
                  return null;
                })}

                {/* Discount Row */}
                {discountPercent > 0 && (
                  <div className="price-row discount-row">
                    <span>Discount ({discountPercent}%)</span>
                    <span className="discount-amount">-€{(calculateTotal() / (1 - discountPercent / 100) * (discountPercent / 100)).toFixed(2)}</span>
                  </div>
                )}

                <hr className="divider-top" />

                <div className="total-row">
                  <span>Total Price:</span>
                  <span className="total-amount">€{calculateTotal()}</span>
                </div>

                {/* Voucher Code */}
                <div className="voucher-wrapper-bottom">
                  <div className="voucher-input-wrapper">
                    <input
                      type="text"
                      className="voucher-input"
                      placeholder="Voucher code"
                      value={voucherInput}
                      onChange={(e) => setVoucherInput(e.target.value)}
                    />
                    {voucherInput && (
                      <button
                        type="button"
                        className="voucher-remove"
                        onClick={removeVoucher}
                      >
                        ✕
                      </button>
                    )}
                  </div>
                  <button
                    type="button"
                    className="voucher-apply"
                    onClick={applyVoucher}
                  >
                    Apply
                  </button>
                </div>

                {voucherMessage && (
                  <p className={`voucher-message ${discountPercent > 0 ? 'success' : 'error'}`}>
                    {voucherMessage}
                  </p>
                )}

                <button
                  type="submit"
                  className="btn-primary"
                  disabled={loading || (event?.recurring && !formData.eventDate)}
                >
                  {loading ? 'Processing...' : 'Confirm Booking'}
                </button>
              </div>
            </div>
          </div>
        </form>
      </div>
    </>
  );
};

export default BookingPage;
