// src/services/eventService.js
const API_BASE_URL = 'http://localhost:8080/api/events';

const eventService = {
  async getAllEvents() {
    try {
      const response = await fetch(API_BASE_URL);  // ← fetch() mit Klammern

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);  // ← Backticks!
      }

      return await response.json();
    } catch (error) {
      console.error('Error fetching events:', error);
      throw error;
    }
  },

  async getEventById(id) {
    try {
      const response = await fetch(`${API_BASE_URL}/${id}`);  // ← Backticks!

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);  // ← Backticks!
      }

      return await response.json();
    } catch (error) {
      console.error('Error fetching event:', error);
      throw error;
    }
  },
};

export default eventService;