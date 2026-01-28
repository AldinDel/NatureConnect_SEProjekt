import { useState, useEffect } from 'react';
import { useParams, Link, useNavigate } from 'react-router-dom';
import eventService from '../../services/eventService';
import './EventDetails.css';

const EventDetails = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const [event, setEvent] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    loadEventDetails();
  }, [id]);

  const loadEventDetails = async () => {
    try {
      setLoading(true);
      const data = await eventService.getEventById(id);
      setEvent(data);
      setError(null);
    } catch (err) {
      setError('Error loading event.');
      console.error('Error loading event details:', err);
    } finally {
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

  const getCategoryClass = (category) => {
    if (!category) return '';
    return category.replace(/ /g, '-');
  };

  const handleBookNow = () => {
    console.log('Book Now clicked for event:', event.id);
    navigate(`/booking/event/${event.id}`);
  };

  const isEventBookable = () => {
    if (!event) return false;
    if (event.cancelled) return false;

    // For recurring events, check if there are future dates available
    if (event.recurring) {
      const today = new Date();
      today.setHours(0, 0, 0, 0);
      const recurrenceEnd = new Date(event.recurrenceEnd);
      return recurrenceEnd >= today;
    }

    // For non-recurring events, check if the event date is in the future
    const eventDate = new Date(event.date);
    const today = new Date();
    today.setHours(0, 0, 0, 0);
    return eventDate >= today;
  };

  if (loading) {
    return (
      <section className="hero-box">
        <div className="loading">Loading...</div>
      </section>
    );
  }

  if (error || !event) {
    return (
      <section className="hero-box">
        <div className="error">
          <p>{error || 'Event not found'}</p>
          <Link to="/" className="back-link back-detail">❮ Back</Link>
        </div>
      </section>
    );
  }

  return (
    <>
      <section className="hero-box">
        <div className="hero-top">
          <Link to="/" className="back-link back-detail">❮ Back</Link>

          <div
            className="category-tag"
            data-category={event.category}
          >
            {event.category}
          </div>
        </div>

        <h1 className="event-title">{event.title}</h1>

        <div className="hero-grid">
          <div className="hero-left">
            <img
              src={event.imageUrl || '/images/default_event.jpg'}
              alt="Main Event Image"
            />
          </div>

          <div className="hero-right">
            <div className="hero-small-img">
              <img
                src={event.imageUrl || '/images/default_event.jpg'}
                alt="Secondary Event Image"
              />
            </div>

            <div className="hero-map">
              <iframe
                src="https://www.openstreetmap.org/export/embed.html?bbox=9.73,47.4,9.75,47.42&layer=mapnik&marker=47.41,9.74"
                width="100%"
                height="100%"
                frameBorder="0"
                title="Map Location"
              ></iframe>
            </div>
          </div>
        </div>
      </section>

      <main className="page">
        <section className="left">
          <div className="box organizer-box">
            <h3>Organized by</h3>
            <p>{event.organizer}</p>
          </div>

          <div className="box">
            <h3>About this Event</h3>
            <p className="event-detail-description">{event.description}</p>
          </div>

          <div className="box">
            <h3>Who can Join</h3>
            <p>{event.audience}</p>
          </div>

          <div className="box">
            <h3>Event Details</h3>

            <div className="details-grid">
              <div className="detail-item">
                <h4>Difficulty</h4>
                <p>{event.difficulty}</p>
              </div>

              <div className="detail-item">
                <h4>Time</h4>
                <p>
                  {event.startTime && formatTime(event.startTime)}
                  {event.endTime && ` – ${formatTime(event.endTime)}`}
                </p>
              </div>

              <div className="detail-item" style={{ gridColumn: '1 / -1' }}>
                <h4>{event.recurring ? 'Schedule' : 'Date'}</h4>
                {!event.recurring && (
                  <span>
                    {formatDate(event.date)}
                    {event.endDate && event.endDate !== event.date && (
                      <> - {formatDate(event.endDate)}</>
                    )}
                  </span>
                )}

                {event.recurring && (
                  <div>
                    <p>
                      Repeats from{' '}
                      <strong>{formatDate(event.recurrenceStart)}</strong> to{' '}
                      <strong>{formatDate(event.recurrenceEnd)}</strong>
                    </p>
                    {event.recurrenceDays && event.recurrenceDays.length > 0 && (
                      <p>
                        {event.recurrenceDays.map((day, idx) => (
                          <span key={idx}>
                            {day}
                            {idx < event.recurrenceDays.length - 1 && ', '}
                          </span>
                        ))}
                      </p>
                    )}
                  </div>
                )}
              </div>
            </div>
          </div>

          {event.equipments && event.equipments.length > 0 && (
            <div className="box">
              <h3>Equipment Add-ons</h3>
              <div className="sub-box">
                <div className="equipment-table">
                  <div className="equipment-header">
                    <span>Name</span>
                    <span>Required</span>
                    <span>Rentable</span>
                    <span>Price</span>
                    <span>Stock</span>
                  </div>

                  <div className="equipment-rows">
                    {event.equipments.map((eq, idx) => (
                      <div key={idx} className="equipment-row">
                        <span>{eq.name}</span>
                        <span>
                          {eq.required ? (
                            <span style={{ color: '#b91c1c', fontWeight: 600 }}>
                              Mandatory
                            </span>
                          ) : (
                            'Optional'
                          )}
                        </span>
                        <span>
                          {eq.rentable ? (
                            <span style={{ color: 'green', fontWeight: 600 }}>✔</span>
                          ) : (
                            <span style={{ color: '#999' }}>—</span>
                          )}
                        </span>
                        <span>
                          {eq.rentable ? `€ ${eq.unitPrice || 0}` : '—'}
                        </span>
                        <span>{eq.stock != null ? eq.stock : '—'}</span>
                      </div>
                    ))}
                  </div>
                </div>
              </div>
            </div>
          )}
        </section>

        <aside className="right">
          <div className="box participants-box">
            <h3>Participants</h3>
            <table className="participant-table">
              <tbody>
                <tr>
                  <td>Minimum</td>
                  <td>{event.minParticipants} people</td>
                </tr>
                <tr>
                  <td>Maximum</td>
                  <td>{event.maxParticipants} people</td>
                </tr>
              </tbody>
            </table>
          </div>

          <div className="box pricing-box">
            <h3>Pricing</h3>
            <p>Price per person</p>
            <p className="price-amount">
              <span>{event.price}</span> €
            </p>

            {isEventBookable() && (
              <button
                  className="book-btn"
                  onClick={handleBookNow}
                  type="button"
              >
                Book now
              </button>
            )}

            {event.cancelled && (
              <div style={{ color: '#b91c1c', fontWeight: 'bold', marginTop: '12px' }}>
                This event has been cancelled
              </div>
            )}

            {!event.cancelled && !isEventBookable() && (
              <div style={{ color: '#6b7280', fontWeight: '500', marginTop: '12px' }}>
                This event is no longer available for booking
              </div>
            )}
          </div>
        </aside>
      </main>
    </>
  );
};

export default EventDetails;
