

export async function fetchGenres() {
    const res = await fetch("http://localhost:8080/genres");
    return res.json();
}

export async function searchArtistsByName(name) {
    const res = await fetch(`http://localhost:8080/discovery/search?name=${encodeURIComponent(name)}`);
    return res.json();
}

export async function fetchFilteredArtists(filterData) {
    const res = await fetch("http://localhost:8080/discovery/filter", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(filterData),
    });
    return res.json();
}
