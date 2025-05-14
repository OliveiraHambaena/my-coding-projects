import React, { useEffect, useState } from "react";

function MovieDetail({ movieId }) {
  const [trailerKey, setTrailerKey] = useState(null);

  useEffect(() => {
    async function fetchTrailer() {
      const res = await fetch(
        `https://api.themoviedb.org/3/movie/${movieId}/videos?api_key=4373e6fe68a1c3efa51b940c67c1e1b2`
      );
      const data = await res.json();
      console.log(data.results); // Add this line
      const trailer = data.results.find(
        (vid) => vid.type === "Trailer" && vid.site === "YouTube"
      );
      if (trailer) setTrailerKey(trailer.key);
      else setTrailerKey(null);
    }
    fetchTrailer();
  }, [movieId]);

  return (
    <div>
      {trailerKey ? (
        <iframe
          width="560"
          height="315"
          src={`https://www.youtube.com/embed/${trailerKey}`}
          title="Movie Trailer"
          frameBorder="0"
          allow="autoplay; encrypted-media"
          allowFullScreen
        ></iframe>
      ) : (
        <p>No trailer available.</p>
      )}
    </div>
  );
}

export default MovieDetail;
