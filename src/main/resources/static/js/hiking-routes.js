document.addEventListener("DOMContentLoaded", () => {
    const box = document.getElementById("hiking-route-box");
    if (!box) return;

    const filterEl = document.getElementById("hike-filter-select");
    const noInfo = document.getElementById("no-hike-info");
    const content = document.getElementById("hike-route-content");
    const listEl = document.getElementById("hike-waypoint-list");
    const selectEl = document.getElementById("hike-route-select");
    const metaEl = document.getElementById("hike-route-meta");
    const graphEl = document.getElementById("hike-route-graph");
    const edgeInfo = document.getElementById("hike-edge-info");

    let routes = [];

    const setEdgeInfo = (text) => {
        if (!edgeInfo) return;
        edgeInfo.style.display = text ? "block" : "none";
        edgeInfo.textContent = text || "";
    };

    fetch("/api/hiking/routes")
        .then(r => r.json())
        .then(data => {
            if (!Array.isArray(data) || data.length === 0) return;

            routes = data;
            noInfo.style.display = "none";
            content.style.display = "block";

            selectEl.innerHTML = routes.map(r => {
                const label = r.difficulty ? `${r.name} (${r.difficulty})` : r.name;
                return `<option value="${r.key}">${label}</option>`;
            }).join("");

            if (filterEl) {
                filterEl.value = "shortest";
                loadBest("shortest");
                filterEl.addEventListener("change", e => {
                    const v = e.target.value;
                    if (v !== " ") loadBest(v);
                });
            }

            render(routes[0]);
            selectEl.addEventListener("change", e => {
                if (filterEl) filterEl.value = " ";
                render(routes.find(r => r.key === e.target.value));
            });
        });

    function loadBest(filter) {
        fetch(`/api/hiking/routes/best?filter=${encodeURIComponent(filter)}`)
            .then(r => r.json())
            .then(route => route && render(route));
    }

    function render(route) {
        if (!route) return;

        selectEl.value = route.key;

        metaEl.textContent = [
            route.difficulty ? `Difficulty: ${route.difficulty}` : null,
            route.lengthKm != null ? `Length: ${route.lengthKm} km` : null,
            route.durationMinutes != null ? `Duration: ${route.durationMinutes} min` : null
        ].filter(Boolean).join(" · ");

        const wps = Array.isArray(route.waypoints) ? [...route.waypoints] : [];
        wps.sort((a, b) => a.sequence - b.sequence);

        listEl.innerHTML = wps.map(wp => `<li>${wp.sequence} – ${wp.name}</li>`).join("");

        renderGraph(route.key);
    }

    function renderGraph(key) {
        if (!graphEl) return;

        setEdgeInfo("");

        fetch(`/api/hiking/routes/${encodeURIComponent(key)}/graph`)
            .then(r => r.json())
            .then(data => {
                const nodes = new vis.DataSet((data.nodes || []).map(n => ({ id: n.id, label: n.label })));
                const edges = new vis.DataSet((data.edges || []).map(e => ({ from: e.from, to: e.to })));

                const network = new vis.Network(graphEl, { nodes, edges }, {
                    layout: { hierarchical: { direction: "LR" } },
                    physics: false,
                    interaction: { dragNodes: false, dragView: false, zoomView: false }
                });

                network.on("click", params => {
                    if (!params.edges || params.edges.length !== 1) return setEdgeInfo("");
                    const e = edges.get(params.edges[0]);
                    const fromLabel = (nodes.get(e.from) || {}).label || e.from;
                    const toLabel = (nodes.get(e.to) || {}).label || e.to;
                    setEdgeInfo(`Segment: ${fromLabel} → ${toLabel}`);
                });
            });
    }
});