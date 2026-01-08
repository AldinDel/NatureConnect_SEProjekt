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

    const allowedKeys = Array.isArray(window.selectedHikeKeys) ? window.selectedHikeKeys : [];
    if (allowedKeys.length === 0) {
        noInfo.style.display = "block";
        content.style.display = "none";
        return;
    }

    const setEdgeInfo = (text) => {
        if (!edgeInfo) return;
        edgeInfo.style.display = text ? "block" : "none";
        edgeInfo.textContent = text || "";
    };

    fetch("/api/hiking/routes")
        .then(r => r.json())
        .then(data => {
            if (!Array.isArray(data) || data.length === 0) return;

            routes = data.filter(r => allowedKeys.includes(r.key));

            if (routes.length === 0) {
                noInfo.style.display = "block";
                content.style.display = "none";
                return;
            }

            noInfo.style.display = "none";
            content.style.display = "block";

            updateFilterOptions();


            selectEl.innerHTML = routes.map(r => {
                const label = r.difficulty ? `${r.name} (${r.difficulty})` : r.name;
                return `<option value="${r.key}">${label}</option>`;
            }).join("");

            if (filterEl) {
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
        if (!Array.isArray(routes) || routes.length === 0) return;
        if (!filterEl) return;

        const hasOpt = Array.from(filterEl.options).some(o => o.value === filter);
        if (!hasOpt) return;

        const diffRank = (d) => {
            const v = String(d || "").toUpperCase();
            if (v === "BEGINNER") return 1;
            if (v === "INTERMEDIATE") return 2;
            if (v === "ADVANCED") return 3;
            return 99;
        };

        let best = routes[0];

        if (filter === "shortest") {
            best = routes.reduce((a, b) => (a.lengthKm ?? 9999) <= (b.lengthKm ?? 9999) ? a : b);
        } else if (filter === "longest") {
            best = routes.reduce((a, b) => (a.lengthKm ?? -1) >= (b.lengthKm ?? -1) ? a : b);
        } else if (filter === "easiest") {
            best = routes.reduce((a, b) => diffRank(a.difficulty) <= diffRank(b.difficulty) ? a : b);
        } else if (filter === "hardest") {
            best = routes.reduce((a, b) => diffRank(a.difficulty) >= diffRank(b.difficulty) ? a : b);
        }

        render(best);
    }

    function updateFilterOptions() {
        if (!filterEl) return;

        // wenn 0/1 route -> filter macht keinen sinn
        if (!Array.isArray(routes) || routes.length <= 1) {
            filterEl.innerHTML = `<option value=" ">—</option>`;
            filterEl.value = " ";
            filterEl.disabled = true;
            return;
        }

        filterEl.disabled = false;

        const lengths = routes
            .map(r => r.lengthKm)
            .filter(v => v != null);

        const diffs = routes
            .map(r => (r.difficulty || "").toUpperCase())
            .filter(v => v);

        const hasDifferentLengths = lengths.length > 1 && new Set(lengths).size > 1;
        const hasDifferentDiffs = diffs.length > 1 && new Set(diffs).size > 1;

        const options = [];
        if (hasDifferentLengths) {
            options.push(`<option value="shortest">Shortest</option>`);
            options.push(`<option value="longest">Longest</option>`);
        }
        if (hasDifferentDiffs) {
            options.push(`<option value="easiest">Easiest</option>`);
            options.push(`<option value="hardest">Hardest</option>`);
        }

        options.push(`<option value=" ">—</option>`);

        filterEl.innerHTML = options.join("");
        filterEl.value = " ";
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
                const nodes = new vis.DataSet((data.nodes || []).map(n => ({id: n.id, label: n.label})));
                const edges = new vis.DataSet((data.edges || []).map(e => ({from: e.from, to: e.to})));

                const network = new vis.Network(graphEl, {nodes, edges}, {
                    layout: {hierarchical: {direction: "LR"}},
                    physics: false,
                    interaction: {dragNodes: false, dragView: false, zoomView: false}
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