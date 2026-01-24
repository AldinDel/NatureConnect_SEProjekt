# NatureConnect React Frontend

React SPA Frontend für das NatureConnect Outdoor-Event Management System.

## Tech Stack

- **React 18** - UI Framework
- **Vite** - Build Tool & Dev Server
- **React Router** - Client-side Routing
- **Axios** - HTTP Client
- **CSS3** - Styling

## Projekt-Struktur

```
frontend/
├── src/
│   ├── components/
│   │   ├── events/
│   │   │   ├── EventList.jsx        # Event-Liste mit Sortierung
│   │   │   ├── EventCard.jsx        # Event-Karten Komponente
│   │   ├── booking/
│   │   │   └── BookingButton.jsx    # Buchungs-Komponente
│   │   └── common/
│   │       └── SearchBar.jsx        # Such-Komponente
│   ├── pages/
│   │   └── events/
│   │       └── EventDetails.jsx     # Event-Detailseite
│   ├── services/
│   │   └── eventService.js          # API-Service für Backend-Kommunikation
│   ├── App.jsx                      # Haupt-App-Komponente mit Routing
│   ├── App.css                      # App-Styles
│   ├── main.jsx                     # React Entry Point
│   └── index.css                    # Globale Styles
├── package.json
├── vite.config.js                   # Vite-Konfiguration mit Proxy
└── README.md
```

## Installation

### Voraussetzungen

- Node.js (v16 oder höher)
- npm oder yarn
- Backend muss auf Port 8080 laufen

### Setup

1. In das Frontend-Verzeichnis wechseln:
```bash
cd frontend
```

2. Dependencies installieren:
```bash
npm install
```

## Development

### Dev Server starten

```bash
npm run dev
```

Die App läuft auf: **http://localhost:6060**

### Backend-Verbindung

Der Dev Server verwendet einen Proxy, um API-Requests an das Backend weiterzuleiten:
- Frontend: `http://localhost:6060`
- Backend: `http://localhost:8080`
- API-Requests an `/api/*` werden automatisch an das Backend weitergeleitet

## Features

### Implementierte Funktionen

1. **Event-Liste**
   - Anzeige aller Events
   - Sortierung nach: Datum, Titel, Preis
   - Responsive Grid-Layout

2. **Event-Suche**
   - Echtzeit-Filterung nach:
     - Titel
     - Beschreibung
     - Ort
     - Kategorie

3. **Event-Details**
   - Vollständige Event-Informationen
   - Equipment-Liste
   - Wiederkehrende Events

4. **Event-Buchung**
   - Buchungsformular mit Modal
   - Validierung
   - Erfolgsbestätigung

### Backend API Endpoints

Die folgenden REST-Endpoints werden verwendet:

```
GET  /api/events              - Alle Events abrufen
GET  /api/events/{id}         - Event-Details abrufen
POST /api/bookings            - Neue Buchung erstellen
GET  /api/bookings/payment-methods - Zahlungsmethoden abrufen
```

## Build für Production

```bash
npm run build
```

Die Build-Dateien werden im `dist/` Verzeichnis erstellt.

### Preview des Production Builds

```bash
npm run preview
```

## Verfügbare Scripts

- `npm run dev` - Startet den Development Server
- `npm run build` - Erstellt Production Build
- `npm run preview` - Preview des Production Builds
- `npm run lint` - Führt ESLint aus

## Komponenten-Übersicht

### EventList
Zeigt alle Events als Grid an mit Such- und Sortierfunktion.

**Features:**
- Echtzeit-Suche
- Sortierung (Datum, Titel, Preis)
- Responsive Design
- Loading & Error States

### EventCard
Einzelne Event-Karte mit wichtigsten Informationen.

**Props:**
- `event` - Event-Objekt mit allen Details

### EventDetails
Detailansicht eines einzelnen Events.

**Features:**
- Vollständige Event-Information
- Equipment-Liste
- Wiederkehrende Events
- Buchungs-Button

### BookingButton
Button mit Modal für Event-Buchungen.

**Props:**
- `event` - Event-Objekt für Buchung

**Features:**
- Buchungsformular
- Validierung
- Success/Error Feedback

### SearchBar
Suchfeld mit Clear-Button.

**Props:**
- `onSearch` - Callback für Sucheingabe
- `placeholder` - Platzhalter-Text

## Konfiguration

### Vite Config

In `vite.config.js` ist der Proxy konfiguriert:

```javascript
export default defineConfig({
  plugins: [react()],
  server: {
    port: 6060,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      }
    }
  }
})
```

## Troubleshooting

### Backend nicht erreichbar

Stelle sicher, dass:
1. Das Spring Boot Backend läuft (http://localhost:8080)
2. Die Proxy-Konfiguration in vite.config.js korrekt ist
3. CORS im Backend richtig konfiguriert ist

### Port 6060 bereits belegt

Ändere den Port in vite.config.js:
```javascript
server: {
  port: 3000, // Anderen Port verwenden
  ...
}
```

## Zukünftige Erweiterungen

- Authentifizierung & Authorization
- Benutzer-Profile
- Buchungs-Historie
- Event-Favoriten
- Admin-Dashboard
- Erweiterte Filter-Optionen
- Mehrsprachigkeit (i18n)
