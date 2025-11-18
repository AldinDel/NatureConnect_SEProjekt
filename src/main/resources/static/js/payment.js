document.querySelectorAll(".radio-label").forEach(label => {
    const input = label.querySelector("input");

    input.addEventListener("change", () => {
        document.querySelectorAll(".radio-label").forEach(l => l.classList.remove("active"));
        label.classList.add("active");
    });
});
