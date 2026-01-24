import { BrowserRouter as Router, Routes, Route } from 'react-router-dom'
import Navbar from './components/layout/Navbar'
import EventList from './components/events/EventList'
import EventDetails from './pages/events/EventDetails'
import BookingPage from './pages/booking/BookingPage'
import PaymentPage from './pages/payment/PaymentPage'
import './App.css'

function App() {
  return (
    <Router>
      <div className="app">
        <Navbar />

        <main className="app-main">
          <Routes>
            <Route path="/" element={<EventList />} />
            <Route path="/events" element={<EventList />} />
            <Route path="/events/:id" element={<EventDetails />} />
            <Route path="/booking/event/:eventId" element={<BookingPage />} />
            <Route path="/booking/payment/:bookingId" element={<PaymentPage />} />
          </Routes>
        </main>

        <footer className="app-footer">
          <p>&copy; 2025 NatureConnect. All rights reserved.</p>
        </footer>
      </div>
    </Router>
  )
}

export default App
