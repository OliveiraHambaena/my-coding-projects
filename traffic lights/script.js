const redLight = document.getElementById('redLight');
        const yellowLight = document.getElementById('yellowLight');
        const greenLight = document.getElementById('greenLight');

        function activateLight(light) {
            light.classList.add('active');
        }

        function deactivateLight(light) {
            light.classList.remove('active');
        }

        function cycleLights() {
            activateLight(redLight);
            setTimeout(() => {
                deactivateLight(redLight);
                activateLight(yellowLight);
                setTimeout(() => {
                    deactivateLight(yellowLight);
                    activateLight(greenLight);
                    setTimeout(() => {
                        deactivateLight(greenLight);
                        activateLight(yellowLight);
                        setTimeout(() => {
                            deactivateLight(yellowLight);
                            cycleLights(); // Restart the cycle
                        }, 1000); // Yellow light duration
                    }, 2000); // Green light duration
                }, 1000);  // Yellow light duration
            }, 3000); // Red light duration
        }

        cycleLights(); // Start the traffic light cycle