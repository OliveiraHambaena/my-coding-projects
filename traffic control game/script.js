const redLight = document.getElementById('redLight');
const yellowLight = document.getElementById('yellowLight');
const greenLight = document.getElementById('greenLight');
const changeLightButton = document.getElementById('changeLightButton');
const scoreDisplay = document.getElementById('score');
const trafficScene = document.querySelector('.traffic-scene');

let currentLight = 'red';
let score = 0;
let cars = [];

function activateLight(light) { light.classList.add('active'); }
function deactivateLight(light) { light.classList.remove('active'); }

function changeLight() {
    deactivateLight(currentLight === 'red' ? redLight : (currentLight === 'yellow' ? yellowLight : greenLight));
    currentLight = (currentLight === 'red' ? 'yellow' : (currentLight === 'yellow' ? 'green' : 'red'));
    activateLight(currentLight === 'red' ? redLight : (currentLight === 'yellow' ? yellowLight : greenLight));
}

changeLightButton.addEventListener('click', changeLight);

function createCar(x, y, direction, speed) {
    const car = document.createElement('div');
    car.classList.add('car');
    car.style.left = x + 'px';
    car.style.top = y + 'px';
    trafficScene.appendChild(car);
    cars.push({ element: car, x, y, direction, speed });
}

createCar(10, 135, 'right', 2); // Horizontal car, going right
createCar(370, 165, 'left', 3); // Horizontal car, going left
createCar(185, 10, 'down', 2); // Vertical car, going down
createCar(215, 270, 'up', 2); // Vertical car, going up

setInterval(() => {
    cars.forEach(car => {
        if (car.direction === 'right') {
            car.x += car.speed;
            car.element.style.left = car.x + 'px';
            if (car.x > 400) { // Remove car when it goes off-screen
                car.element.remove();
                cars.splice(cars.indexOf(car), 1);
            }
        } else if (car.direction === 'left') {
            car.x -= car.speed;
            car.element.style.left = car.x + 'px';
            if (car.x < -20) { // Remove car when it goes off-screen
                car.element.remove();
                cars.splice(cars.indexOf(car), 1);
            }
        } else if (car.direction === 'down') {
            car.y += car.speed;
            car.element.style.top = car.y + 'px';
            if (car.y > 300) { // Remove car when it goes off-screen
                car.element.remove();
                cars.splice(cars.indexOf(car), 1);
            }
        } else if (car.direction === 'up') {
            car.y -= car.speed;
            car.element.style.top = car.y + 'px';
            if (car.y < -10) { // Remove car when it goes off-screen
                car.element.remove();
                cars.splice(cars.indexOf(car), 1);
            }
        }
    });
}, 30);