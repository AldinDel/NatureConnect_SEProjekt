document.addEventListener("DOMContentLoaded", async () => {
    const iframe = document.getElementById("eventMap");
    if (!iframe) return;

    const location = (iframe.dataset.location || "").trim();
    if (!location) return;

    try {
        const url = `https://nominatim.openstreetmap.org/search?format=json&limit=1&q=${encodeURIComponent(location)}`;
        const res = await fetch(url, {
            headers: {
                "Accept": "application/json"
            }
        });

        if (!res.ok) return;
        const data = await res.json();
        if (!data || data.length === 0) return;

        const lat = parseFloat(data[0].lat);
        const lon = parseFloat(data[0].lon);

        const dLat = 0.04;
        const dLon = 0.06;

        const left = lon - dLon;
        const right = lon + dLon;
        const bottom = lat - dLat;
        const top = lat + dLat;

        const embed = `https://www.openstreetmap.org/export/embed.html?bbox=${left},${bottom},${right},${top}&layer=mapnik&marker=${lat},${lon}`;
        iframe.src = embed;
    } catch (e) {
    }
});
