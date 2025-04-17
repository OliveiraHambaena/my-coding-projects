import MovieCard from "../Components/MovieCard";
import { useState } from "react";

function Home() {
    const [searchQuery, setSearchQuery] = useState("");

    const movies = [
        { id: 1, title: "James Bond", release_date: "2023" },
        { id: 2, title: "The Irishman", release_date: "2021" },
        { id: 3, title: "Batman vs Superman", release_date: "2004" },
    ];

    const handleSearch = () => {
        e.preventDefault()
        alert(searchQuery)
        setSearchQuery("")
    };
        return (
            <div className="home">
                <form onSubmit={handleSearch} className="search-form">
                    <input 
                    type="text" 
                    placeholder="Search for movies..." 
                    className="search-input"
                    value={searchQuery}
                    onChange={(e) => setSearchQuery(e.target.value)}
                    />
                    <button type="submit" className="search-button">
                        Search
                    </button>
                </form>

                <div className="movie-grid">
                    {movies.map(
                        (movie) => 
                        movie.title.toLowerCase().startsWith(searchQuery) && (
                        <MovieCard movie={movie} key={movie.id} />
                    )
                )}
                </div>
            </div>
        );
    };

export default Home;
