document.addEventListener('DOMContentLoaded', function() {
    // Configuration
    const API_KEY = 'bmSlnWzmJmo6o4UJVNzDcPBX4F3h3r8s'; // Your AccuWeather API key
    const BASE_URL = 'https://dataservice.accuweather.com';
    
    // DOM Elements
    const container = document.querySelector('.container');
    const searchBtn = document.querySelector('.search-btn');
    const searchInput = document.querySelector('.search-box input');
    const weatherIcon = document.querySelector('.weather-icon');
    const temperature = document.querySelector('.temperature');
    const description = document.querySelector('.description');
    const humidityValue = document.querySelector('.humidity-value');
    const windValue = document.querySelector('.wind-value');
    const weatherBox = document.querySelector('.weather-box');
    const weatherDetails = document.querySelector('.weather-details');
    const error404 = document.querySelector('.not-found');

    // Weather icon mapping
    const weatherIconMap = {
        1: 'clear.png',  2: 'clear.png',   // Sunny/Mostly Sunny
        3: 'cloud.png',  4: 'cloud.png',    // Partly Sunny/Intermittent Clouds
        5: 'cloud.png',  6: 'cloud.png',    // Hazy Sunshine/Mostly Cloudy
        7: 'cloud.png',  8: 'cloud.png',    // Cloudy/Dreary
        11: 'mist.png', 32: 'mist.png',     // Fog/Windy
        12: 'rain.png', 13: 'rain.png',     // Showers
        14: 'rain.png', 18: 'rain.png',     // Rain
        15: 'rain.png', 16: 'rain.png',     // T-Storms
        17: 'rain.png',                     // Partly Sunny w/ T-Storms
        22: 'snow.png', 23: 'snow.png',     // Snow
        29: 'snow.png'                      // Rain and Snow
    };

    // Fetch weather data
    async function fetchWeatherData(city) {
        try {
            setLoadingState(true);
            
            // Get location key
            const locationRes = await fetch(
                `${BASE_URL}/locations/v1/cities/search?apikey=${API_KEY}&q=${city}&language=en-us`
            );
            
            if (!locationRes.ok) throw handleApiError(locationRes.status);
            
            const locationData = await locationRes.json();
            if (!locationData.length) throw new Error('City not found');
            
            // Get weather data
            const weatherRes = await fetch(
                `${BASE_URL}/currentconditions/v1/${locationData[0].Key}?apikey=${API_KEY}&language=en-us&details=true`
            );
            
            if (!weatherRes.ok) throw handleApiError(weatherRes.status);
            
            const weatherData = await weatherRes.json();
            return weatherData[0];
            
        } finally {
            setLoadingState(false);
        }
    }

    // Update UI with weather data
    function updateUI(weather) {
        // Update weather icon
        weatherIcon.src = `images/${weatherIconMap[weather.WeatherIcon] || 'cloud.png'}`;
        
        // Update temperature
        temperature.innerHTML = `${Math.round(weather.Temperature.Metric.Value)}<span>°C</span>`;
        
        // Update description
        description.textContent = weather.WeatherText;
        
        // Update humidity
        humidityValue.textContent = `${weather.RelativeHumidity}%`;
        
        // Update wind speed
        windValue.textContent = `${Math.round(weather.Wind.Speed.Metric.Value)} km/h`;
        
        // Show weather info
        weatherBox.style.display = '';
        weatherDetails.style.display = '';
        error404.style.display = 'none';
        container.style.height = '590px';
        
        // Add animations
        weatherBox.classList.add('fadeIn');
        weatherDetails.classList.add('fadeIn');
    }

    // Show error message
    function showError(message) {
        error404.querySelector('p').textContent = message;
        error404.style.display = 'block';
        weatherBox.style.display = 'none';
        weatherDetails.style.display = 'none';
        container.style.height = '400px';
        error404.classList.add('fadeIn');
    }

    // Set loading state
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

    // Handle API errors
    function handleApiError(status) {
        switch(status) {
            case 400: return new Error('Invalid request');
            case 401: return new Error('Invalid API key');
            case 403: return new Error('Access denied');
            case 404: return new Error('City not found');
            case 500: return new Error('Server error');
            default: return new Error(`API error: ${status}`);
        }
    }

    // Event listeners
    searchBtn.addEventListener('click', async () => {
        const city = searchInput.value.trim();
        if (!city) return;
        
        try {
            const weather = await fetchWeatherData(city);
            updateUI(weather);
        } catch (error) {
            console.error('Error:', error);
            showError(
                error.message.includes('API key') ? 'Invalid API key' :
                error.message.includes('Network') ? 'Network error' :
                error.message
            );
        }
    });

    searchInput.addEventListener('keypress', (e) => {
        if (e.key === 'Enter') searchBtn.click();
    });
});