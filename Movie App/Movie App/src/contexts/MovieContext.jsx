import { createContext, useState, useContext, useEffect, use } from "react"

const MovieContext = createContext()

export const useMovieContext = () => useContext(MovieContext)

export const MovieProvider = ({children}) => {
    const [favourites, setFavourites] = useState([])

    useEffect(() => {
        const storedFavs = localStorage.getItem("favourites")

        if (storedFavs) 
            setFavourites(JSON.parse(storedFavs))
    }, [])

    return <MovieContext>
        {children}
    </MovieContext>
}