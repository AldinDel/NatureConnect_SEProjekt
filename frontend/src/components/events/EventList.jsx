import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import eventService from '../../services/eventService';
import EventCard from './EventCard';
import './EventList.css';

const EventList = () => {
  const [events, setEvents] = useState([]);
  const [filteredEvents, setFilteredEvents] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [searchTerm, setSearchTerm] = useState('');
  const [selectedCategory, setSelectedCategory] = useState('All Events');
  const [sortBy, setSortBy] = useState('default');

  // Alle verfügbaren Kategorien
  const categories = ['All Events', 'Hiking', 'Camping', 'Kayaking', 'Wildlife Tour', 'Climbing', 'Skiing'];

  useEffect(() => {
    loadEvents();
  }, []);

  useEffect(() => {
    filterAndSortEvents();
  }, [events, searchTerm, selectedCategory, sortBy]);

  const loadEvents = async () => {
    try {
      setLoading(true);
      const data = await eventService.getAllEvents();
      setEvents(data);
      setError(null);
    } catch (err) {
      setError('Error loading events. Please try again later.');
      console.error('Error loading events:', err);
    } finally {
      setLoading(false);
    }
  };

  const filterAndSortEvents = () => {
    let result = [...events];

    // Filter nach Kategorie
    if (selectedCategory !== 'All Events') {
      result = result.filter(event => event.category === selectedCategory);
    }

    // Filter nach Suchbegriff
    if (searchTerm) {
      const lowerSearch = searchTerm.toLowerCase();
      result = result.filter(event =>
          event.title?.toLowerCase().includes(lowerSearch) ||
          event.description?.toLowerCase().includes(lowerSearch) ||
          event.location?.toLowerCase().includes(lowerSearch) ||
          event.category?.toLowerCase().includes(lowerSearch)
      );
    }

    // Sortierung
    switch(sortBy) {
      case 'price-asc':
        result.sort((a, b) => (a.price || 0) - (b.price || 0));
        break;
      case 'price-desc':
        result.sort((a, b) => (b.price || 0) - (a.price || 0));
        break;
      case 'date-asc':
        result.sort((a, b) => {
          if (!a.date) return 1;
          if (!b.date) return -1;
          return new Date(a.date) - new Date(b.date);
        });
        break;
      case 'date-desc':
        result.sort((a, b) => {
          if (!a.date) return 1;
          if (!b.date) return -1;
          return new Date(b.date) - new Date(a.date);
        });
        break;
      default:
        break;
    }

    setFilteredEvents(result);
  };

  if (loading) {
    return (
        <section className="event-overview">
          <div className="loading">Loading events...</div>
        </section>
    );
  }

  if (error) {
    return (
        <section className="event-overview">
          <div className="error">
            <p>{error}</p>
            <button onClick={loadEvents} className="retry-button">
              Try again
            </button>
          </div>
        </section>
    );
  }

  return (
      <section className="event-overview" style={{ marginTop: '30px' }}>

        {/* Home Button */}
        <Link to="/" className="back-link" style={{ marginBottom: '30px', display: 'inline-flex' }}>
          ❮ Home
        </Link>

        {/* Überschrift */}
        <h1>Event Overview</h1>
        <p>Discover exciting events and activities near you</p>

        {/* Suchleiste */}
        <form className="search-form" onSubmit={(e) => e.preventDefault()}>
          <div className="search-bar">
            <input
                type="text"
                name="q"
                placeholder="Browse Events..."
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
            />
          </div>
        </form>

        {/* Kategorie-Filter */}
        <div className="category-bar">
          {categories.map(category => (
              <button
                  key={category}
                  className={selectedCategory === category ? 'active' : ''}
                  onClick={() => setSelectedCategory(category)}
              >
                {category}
              </button>
          ))}
        </div>

        {/* Event-Anzahl und Sortierung */}
        <div className="event-meta-bar">
          <div className="event-count">
            <span>{filteredEvents.length} Events found</span>
          </div>

          <div className="sort-select-wrapper">
            <select value={sortBy} onChange={(e) => setSortBy(e.target.value)}>
              <option value="default">Default</option>
              <option value="date-asc">Date (Earliest)</option>
              <option value="date-desc">Date (Latest)</option>
              <option value="price-asc">Price (Low to High)</option>
              <option value="price-desc">Price (High to Low)</option>
            </select>
          </div>
        </div>

        {/* Event-Grid */}
        <div className="event-grid">
          {filteredEvents.map(event => (
              <EventCard key={event.id} event={event} />
          ))}
        </div>

        {/* Keine Events gefunden */}
        {filteredEvents.length === 0 && (
            <div className="no-events">
              <p>No events found matching your criteria.</p>
              <button
                  onClick={() => {
                    setSearchTerm('');
                    setSelectedCategory('All Events');
                    setSortBy('default');
                  }}
                  className="clear-search-button"
              >
                Clear filters
              </button>
            </div>
        )}
      </section>
  );
};

export default EventList;