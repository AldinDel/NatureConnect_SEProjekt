import { Link } from 'react-router-dom';
import './Navbar.css';

const Navbar = () => {
  return (
    <header className="nc-header">
      <div className="nc-container">

        <div className="nc-top">
          <Link to="/" className="nc-logo">
            <img src="/images/NatureConnectLogo.png" alt="Logo" />
            <span className="nc-brand"><strong>NATURE</strong>Connect</span>
          </Link>
        </div>

        <div className="nc-divider"></div>

        <nav className="nc-nav" id="nc-nav">
          <ul className="nc-left">
            <li><Link to="/">Home</Link></li>
            <li><Link to="/events">Events</Link></li>
            <li><Link to="/bookings">Bookings</Link></li>
          </ul>

          <ul className="nc-right">
            <li><Link to="/about">About Us</Link></li>
            <li><span className="divider">|</span></li>
            <li><Link to="/contact">Contact Us</Link></li>
            <li><Link to="/login" className="nc-login">Log In</Link></li>
          </ul>
        </nav>

      </div>
    </header>
  );
};

export default Navbar;
