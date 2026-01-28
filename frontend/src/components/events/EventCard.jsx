import { Link } from 'react-router-dom';
import './EventCard.css';

const EventCard = ({ event }) => {
  // Formatiere das Datum
  const formatDate = (date) => {
    if (!date) return 'Recurring Event';
    const d = new Date(date);
    return d.toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'long',
      day: 'numeric'
    });
  };

  // Category Tag CSS-Klasse
  const getCategoryClass = (category) => {
    if (!category) return 'tag-general';
    const normalized = category.toLowerCase().replace(/ /g, '-');
    return `tag-${normalized}`;
  };

  // Difficulty CSS-Klasse
  const getDifficultyClass = (difficulty) => {
    if (!difficulty) return '';
    return difficulty.toLowerCase();
  };

  return (
      <div className={`event-card ${event.cancelled ? 'cancelled-event' : ''}`}>

        <div className="card-image">
          <img
              src={event.imageUrl || '/images/default_event.jpg'}
              alt={event.title}
          />
          <span className={`tag ${getCategoryClass(event.category)}`}>
            {event.category}
          </span>
          {event.cancelled && (
              <span className="cancelled-badge">CANCELLED</span>
          )}
        </div>

        <div className="card-content">
          <div className="card-header">
            <h3>{event.title}</h3>
            <span className={`difficulty ${getDifficultyClass(event.difficulty)}`}>
              {event.difficulty}
            </span>
          </div>

          <p className="event-description">
            {event.description}
          </p>

          <ul className="details">
            <li>
              <svg xmlns="http://www.w3.org/2000/svg" width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                <rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect>
                <line x1="16" y1="2" x2="16" y2="6"></line>
                <line x1="8" y1="2" x2="8" y2="6"></line>
                <line x1="3" y1="10" x2="21" y2="10"></line>
              </svg>
              <span>{formatDate(event.date)}</span>
            </li>
            <li>
              <svg xmlns="http://www.w3.org/2000/svg" width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                <path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z"></path>
                <circle cx="12" cy="10" r="3"></circle>
              </svg>
              <span>{event.location}</span>
            </li>
            <li style={{ fontSize: '0.8rem', color: '#888' }}>
              <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path>
                <circle cx="12" cy="7" r="4"></circle>
              </svg>
              <span>by {event.organizer || event.displayOrganizer}</span>
            </li>
          </ul>

          <div className="footer">
            <span className="price">€ {event.price}</span>
            <div className="buttons">
              <Link to={`/events/${event.id}`} className="view-btn">
                View Details
              </Link>
            </div>
          </div>
        </div>
      </div>
  );
};

export default EventCard;
