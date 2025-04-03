import React from "react";
import './App.css'
import MovieCard from "./components/MovieCard";

function App() {
    const movie = {
        url: "poster.jpg",
        title: "Inception",
        release_date: "2010"
    };

    return (
        <div className="App">
            <MovieCard movie={movie} />
        </div>
    );
}

export default App;
