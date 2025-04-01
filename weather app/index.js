document.addEventListener('DOMContentLoaded', function () {
    // Configuration
    const API_KEY = 'bmSlnWzmJmo6o4UJVNzDcPBX4F3h3r8s'; // Your AccuWeather API key
    const BASE_URL = 'https://dataservice.accuweather.com';

    // DOM Elements
    const container = document.querySelector('.container');
    const searchInput = document.querySelector('.search-box input');
    const searchBtn = document.querySelector('.search-btn');
    const weatherIcon = document.querySelector('.weather-icon');
    const temperature = document.querySelector('.temperature');
    const description = document.querySelector('.description');
    const humidityValue = document.querySelector('.humidity-value');
    const windValue = document.querySelector('.wind-value');
    const weatherBox = document.querySelector('.weather-box');
    const weatherDetails = document.querySelector('.weather-details');
    const error404 = document.querySelector('.not-found');

    // Create suggestions box
    const suggestionsBox = document.createElement('div');
    suggestionsBox.className = 'suggestions-box';
    container.appendChild(suggestionsBox);

    // Weather icon mapping
    const weatherIconMap = {
        1: 'clear.png', 2: 'clear.png',   // Sunny
        3: 'cloud.png', 4: 'cloud.png',   // Partly Cloudy
        5: 'cloud.png', 6: 'cloud.png',   // Mostly Cloudy
        7: 'cloud.png', 8: 'cloud.png',   // Cloudy
        11: 'mist.png', 32: 'mist.png',   // Fog
        12: 'rain.png', 13: 'rain.png',   // Showers
        14: 'rain.png', 18: 'rain.png',   // Rain
        15: 'rain.png', 16: 'rain.png',   // Thunderstorms
        22: 'snow.png', 23: 'snow.png',  // Snow
        29: 'snow.png'                    // Rain and Snow
    };

    // ================== AUTOCOMPLETE FUNCTIONALITY ==================
    searchInput.addEventListener('input', debounce(async function (e) {
        const query = e.target.value.trim();
        if (query.length < 2) {
            suggestionsBox.style.display = 'none';
            return;
        }

        try {
            const response = await fetch(
                `${BASE_URL}/locations/v1/cities/autocomplete?apikey=${API_KEY}&q=${query}&language=en-us`
            );
            const cities = await response.json();
            showSuggestions(cities);
        } catch (error) {
            console.error('Autocomplete error:', error);
            suggestionsBox.style.display = 'none';
        }
    }, 300));

    function showSuggestions(cities) {
        suggestionsBox.innerHTML = '';
        if (!cities || cities.length === 0) return;

        cities.slice(0, 5).forEach(city => {
            const suggestion = document.createElement('div');
            suggestion.className = 'suggestion';
            suggestion.innerHTML = `
                <strong>${city.LocalizedName}</strong>
                <span>${city.AdministrativeArea.LocalizedName}, ${city.Country.LocalizedName}</span>
            `;
            suggestion.addEventListener('click', () => {
                searchInput.value = city.LocalizedName;
                suggestionsBox.style.display = 'none';
                fetchCurrentConditions(city.Key, city.LocalizedName);
            });
            suggestionsBox.appendChild(suggestion);
        });
        suggestionsBox.style.display = 'block';
    }

    // ================== CURRENT CONDITIONS FETCH ==================
    async function fetchCurrentConditions(locationKey, cityName) {
        try {
            setLoadingState(true);

            const response = await fetch(
                `${BASE_URL}/currentconditions/v1/${locationKey}?apikey=${API_KEY}&language=en-us&details=true`
            );

            if (!response.ok) throw new Error(`API Error: ${response.status}`);

            const [weatherData] = await response.json();
            updateWeatherUI(weatherData, cityName);

        } catch (error) {
            console.error('Weather fetch error:', error);
            showError(
                error.message.includes('401') ? 'Invalid API key' :
                    error.message.includes('404') ? 'City not found' :
                        'Failed to fetch weather data'
            );
        } finally {
            setLoadingState(false);
        }
    }

    // ================== UI UPDATES ==================
    function updateWeatherUI(weather, cityName) {
        // Update weather icon
        weatherIcon.src = `images/${weatherIconMap[weather.WeatherIcon] || 'cloud.png'}`;

        // Update temperature
        temperature.innerHTML = `${Math.round(weather.Temperature.Metric.Value)}<span>°C</span>`;

        // Update description
        description.textContent = weather.WeatherText;

        // Update humidity (from details)
        humidityValue.textContent = `${weather.RelativeHumidity || 'N/A'}%`;

        // Update wind speed (from details)
        windValue.textContent = `${Math.round(weather.Wind.Speed.Metric.Value)} km/h`;

        // Show weather elements
        weatherBox.style.display = '';
        weatherDetails.style.display = '';
        error404.style.display = 'none';
        container.style.height = '590px';

        // Add animations
        weatherBox.classList.add('fadeIn');
        weatherDetails.classList.add('fadeIn');
    }

    function showError(message) {
        error404.querySelector('p').textContent = message;
        error404.style.display = 'block';
        weatherBox.style.display = 'none';
        weatherDetails.style.display = 'none';
        container.style.height = '400px';
        error404.classList.add('fadeIn');
    }

    function setLoadingState(isLoading) {
        if (isLoading) {
            searchBtn.innerHTML = '<img src="icons/spinner.svg" style="height:20px">';
            weatherBox.style.display = 'none';
            weatherDetails.style.display = 'none';
            error404.style.display = 'none';
        } else {
            searchBtn.innerHTML = '<img src="icons/magnifying-glass.svg" alt="Search">';
        }
    }

    // ================== UTILITIES ==================
    function debounce(func, wait) {
        let timeout;
        return function (...args) {
            clearTimeout(timeout);
            timeout = setTimeout(() => func.apply(this, args), wait);
        };
    }

    // Handle search button click
    searchBtn.addEventListener('click', () => {
        const city = searchInput.value.trim();
        if (city) {
            suggestionsBox.style.display = 'none';
            // First try to get location key
            fetch(`${BASE_URL}/locations/v1/cities/search?apikey=${API_KEY}&q=${city}&language=en-us`)
                .then(res => res.json())
                .then(cities => {
                    if (cities && cities.length > 0) {
                        fetchCurrentConditions(cities[0].Key, cities[0].LocalizedName);
                    } else {
                        throw new Error('City not found');
                    }
                })
                .catch(error => showError('Invalid location'));
        }
    });

    // Handle Enter key
    searchInput.addEventListener('keypress', (e) => {
        if (e.key === 'Enter') searchBtn.click();
    });

    // Close suggestions when clicking outside
    document.addEventListener('click', (e) => {
        if (!container.contains(e.target)) {
            suggestionsBox.style.display = 'none';
        }
    });
});