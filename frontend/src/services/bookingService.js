const API_BASE_URL = 'http://localhost:8080/api/bookings';

const bookingService = {
  // Erstellt eine neue Buchung
  async createBooking(bookingData) {
    const response = await fetch(API_BASE_URL, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(bookingData),
    });

    if (!response.ok) {
      const error = await response.text();
      throw new Error(error || 'Failed to create booking');
    }

    return response.json();
  },

  // Holt eine Buchung nach ID
  async getBookingById(id) {
    const response = await fetch(`${API_BASE_URL}/${id}`);

    if (!response.ok) {
      throw new Error('Failed to fetch booking');
    }

    return response.json();
  },

  // Aktualisiert die Zahlungsmethode
  async updatePaymentMethod(id, paymentMethod) {
    const response = await fetch(`${API_BASE_URL}/${id}/payment?paymentMethod=${paymentMethod}`, {
      method: 'POST',
    });

    if (!response.ok) {
      throw new Error('Failed to update payment method');
    }

    return response.json();
  },

  // Holt alle verfügbaren Zahlungsmethoden
  async getPaymentMethods() {
    const response = await fetch(`${API_BASE_URL}/payment-methods`);

    if (!response.ok) {
      throw new Error('Failed to fetch payment methods');
    }

    return response.json();
  },

  // Holt Rückerstattungs-Vorschau
  async getRefundPreview(id) {
    const response = await fetch(`${API_BASE_URL}/${id}/refund-preview`);

    if (!response.ok) {
      throw new Error('Failed to fetch refund preview');
    }

    return response.json();
  },
};

export default bookingService;
