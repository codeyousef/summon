// Manual Summon Hydration Script for debugging
console.log('=== SUMMON HYDRATION CLIENT INITIALIZING ===');
console.log('Browser user agent:', navigator.userAgent);
console.log('Current URL:', window.location.href);
console.log('Document ready state:', document.readyState);

function startHydration() {
    try {
        console.log('Starting Summon component hydration...');
        console.log('Document ready state:', document.readyState);
        console.log('Document body exists:', document.body !== null);
        
        // Load hydration data
        const hydrationData = loadHydrationData();
        console.log('Hydration data loaded:', hydrationData !== null);
        if (hydrationData) {
            console.log('Hydration data version:', hydrationData.version);
            console.log('Callbacks available:', hydrationData.callbacks.length);
            hydrationData.callbacks.forEach(callbackId => {
                console.log('Available callback:', callbackId);
            });
        }
        
        // Check for button elements BEFORE hydration
        const allButtons = document.querySelectorAll('button');
        console.log('Total buttons found on page:', allButtons.length);
        
        const clickableButtons = document.querySelectorAll('button[data-onclick-action="true"]');
        console.log('Buttons with data-onclick-action="true":', clickableButtons.length);
        
        for (let i = 0; i < clickableButtons.length; i++) {
            const button = clickableButtons[i];
            const callbackId = button.getAttribute('data-onclick-id');
            const hasAction = button.getAttribute('data-onclick-action');
            console.log(`Button ${i}: callbackId='${callbackId}', hasAction='${hasAction}'`);
            console.log(`Button ${i} HTML:`, button.outerHTML);
        }
        
        // Hydrate all interactive elements
        hydrateClickHandlers(hydrationData);
        
        // Verify hydration worked
        const hydratedButtons = document.querySelectorAll('button[data-onclick-action="true"]');
        console.log('Post-hydration: buttons with click handlers:', hydratedButtons.length);
        
        console.log('Summon hydration completed successfully');
    } catch (e) {
        console.error('Summon hydration failed:', e.message);
        console.error('Exception:', e);
    }
}

function loadHydrationData() {
    const dataElement = document.getElementById('summon-hydration-data');
    if (!dataElement) {
        console.warn('No Summon hydration data found');
        return null;
    }
    
    const jsonText = dataElement.textContent || '';
    try {
        return parseHydrationData(jsonText);
    } catch (e) {
        console.error('Failed to parse hydration data:', e.message);
        return null;
    }
}

function parseHydrationData(jsonText) {
    const parsed = JSON.parse(jsonText);
    const callbacks = parsed.callbacks;
    
    // Handle both formats: simple array (core) or complex object (Spring Boot)
    const callbackIds = Array.isArray(callbacks) 
        ? callbacks // Simple array format: ["callback-id-1", "callback-id-2"]
        : Object.keys(callbacks); // Complex object format: {"callback-id": {"type": "login", ...}}
    
    return {
        version: parsed.version,
        callbacks: callbackIds,
        timestamp: parsed.timestamp
    };
}

function hydrateClickHandlers(hydrationData) {
    console.log('Starting to hydrate click handlers...');
    
    // Find all elements with click actions
    const clickableElements = document.querySelectorAll('[data-onclick-action="true"]');
    console.log('Found', clickableElements.length, 'elements with data-onclick-action="true"');
    
    let successCount = 0;
    let errorCount = 0;
    
    for (let i = 0; i < clickableElements.length; i++) {
        const element = clickableElements[i];
        const callbackId = element.getAttribute('data-onclick-id');
        const tagName = element.tagName.toLowerCase();
        
        console.log(`Processing element ${i}: <${tagName}> with callbackId='${callbackId}'`);
        
        if (callbackId) {
            // Verify the callback exists in our hydration data
            if (hydrationData && hydrationData.callbacks.includes(callbackId)) {
                console.log(`Callback ${callbackId} found in hydration data, adding event listener...`);
                
                element.addEventListener('click', function(event) {
                    console.log('=== CLICK EVENT TRIGGERED ===');
                    console.log('Button clicked! callbackId:', callbackId);
                    console.log('Event target:', event.target);
                    console.log('Event type:', event.type);
                    console.log('About to prevent default and handle click...');
                    event.preventDefault();
                    handleClick(callbackId);
                });
                
                // Test that the event listener was added
                console.log('Event listener added successfully for:', callbackId);
                
                // Test if the element is clickable
                console.log('Element position:', element.getBoundingClientRect());
                console.log('Element z-index:', window.getComputedStyle(element).zIndex);
                console.log('Element pointer-events:', window.getComputedStyle(element).pointerEvents);
                console.log('Element visibility:', window.getComputedStyle(element).visibility);
                console.log('Element display:', window.getComputedStyle(element).display);
                
                // Add a test click handler to debug
                element.addEventListener('mousedown', function(e) {
                    console.log('MOUSEDOWN detected on button:', callbackId);
                });
                
                element.addEventListener('mouseup', function(e) {
                    console.log('MOUSEUP detected on button:', callbackId);
                });
                
                successCount++;
            } else {
                console.warn('Callback not found in hydration data:', callbackId);
                if (!hydrationData) {
                    console.warn('  - Hydration data is null');
                } else {
                    console.warn('  - Available callbacks:', hydrationData.callbacks);
                }
                errorCount++;
            }
        } else {
            console.warn(`Element missing data-onclick-id attribute: <${tagName}>`);
            errorCount++;
        }
    }
    
    console.log('Click handler hydration complete:');
    console.log('  - Successfully hydrated:', successCount, 'elements');
    console.log('  - Errors:', errorCount, 'elements');
    console.log('  - Total processed:', clickableElements.length, 'elements');
}

function handleClick(callbackId) {
    console.log('=== BUTTON CLICK DETECTED ===');
    console.log('Executing callback:', callbackId);
    console.log('Current URL:', window.location.href);
    console.log('About to make server request...');
    
    executeCallbackOnServer(callbackId);
}

function executeCallbackOnServer(callbackId) {
    console.log('=== MAKING SERVER REQUEST ===');
    
    try {
        // Capture form data if this is an add-todo callback
        let formData = null;
        if (callbackId.startsWith('add-todo-')) {
            console.log('DEBUG: This is an add-todo callback, attempting to capture form data...');
            const todoInput = document.getElementById('newTodoInput');
            console.log('DEBUG: Found todo input element:', todoInput);
            if (todoInput) {
                const inputValue = todoInput.value;
                const trimmedValue = inputValue.trim();
                console.log('DEBUG: Input value:', JSON.stringify(inputValue));
                console.log('DEBUG: Trimmed value:', JSON.stringify(trimmedValue));
                
                // If the input is empty, warn the user
                if (trimmedValue === '') {
                    console.warn('WARNING: Todo input field is empty! User needs to type something first.');
                    alert('Please enter a todo item before clicking Add!');
                    return; // Don't send request if input is empty
                }
                
                formData = {
                    todoText: trimmedValue
                };
                console.log('DEBUG: Final form data object:', formData);
            } else {
                console.error('ERROR: Could not find newTodoInput field for add-todo callback!');
                console.log('DEBUG: Available input elements:', document.querySelectorAll('input'));
                console.log('DEBUG: Available elements with id newTodoInput:', document.getElementById('newTodoInput'));
                
                // Try alternative methods to find the input
                const inputsByName = document.querySelectorAll('input[name="todoText"]');
                console.log('DEBUG: Inputs by name="todoText":', inputsByName);
                
                if (inputsByName.length > 0) {
                    const altInput = inputsByName[0];
                    console.log('DEBUG: Using alternative input by name:', altInput);
                    formData = {
                        todoText: altInput.value.trim()
                    };
                    console.log('DEBUG: Alternative form data:', formData);
                }
            }
        }
        
        const requestData = { 
            callbackId: callbackId,
            formData: formData
        };
        const json = JSON.stringify(requestData);
        
        console.log('Request payload:', json);
        console.log('Request URL: /summon/callback');
        console.log('Request method: POST');
        
        console.log('About to send fetch request...');
        
        fetch('/summon/callback', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: json
        })
        .then(function(response) {
            console.log('Server response received - Status:', response.status);
            console.log('Response headers:', response.headers);
            
            if (response.ok) {
                console.log('Response OK, parsing JSON...');
                
                return response.json();
            } else {
                console.error('Server returned error status:', response.status);
                return response.text().then(function(responseText) {
                    console.error('Error response body:', responseText);
                    throw new Error('Server error: ' + response.status);
                });
            }
        })
        .then(function(data) {
            console.log('=== SERVER RESPONSE RECEIVED ===');
            console.log('Callback executed successfully:', callbackId);
            console.log('Response data (full object):', JSON.stringify(data, null, 2));
            
            // Clear input field for successful add-todo operations
            if (callbackId.startsWith('add-todo-') && data.success) {
                const todoInput = document.getElementById('newTodoInput');
                if (todoInput) {
                    todoInput.value = '';
                    console.log('Cleared todo input field');
                }
            }
            
            const action = data.action;
            const redirectUrl = data.redirectUrl;
            
            console.log('=== REDIRECT ACTION PROCESSING ===');
            console.log('Response action:', JSON.stringify(action));
            console.log('Response redirectUrl:', JSON.stringify(redirectUrl));
            console.log('Current URL before redirect:', window.location.href);
            
            switch (action) {
                case 'redirect':
                    if (redirectUrl) {
                        console.log('PERFORMING REDIRECT...');
                        console.log('Redirecting from:', window.location.href);
                        console.log('Redirecting to:', redirectUrl);
                        console.log('About to set window.location.href...');
                        
                        // Add a small delay to ensure logs are visible
                        setTimeout(() => {
                            console.log('EXECUTING: window.location.href = "' + redirectUrl + '"');
                            window.location.href = redirectUrl;
                        }, 100);
                        
                    } else {
                        console.warn('Redirect action but no redirectUrl provided');
                        console.log('FALLING BACK TO RELOAD');
                        window.location.reload();
                    }
                    break;
                case 'reload':
                    console.log('PERFORMING PAGE RELOAD');
                    window.location.reload();
                    break;
                default:
                    console.log('DEFAULT ACTION: reloading page');
                    console.log('Action was:', JSON.stringify(action));
                    window.location.reload();
                    break;
            }
        })
        .catch(function(error) {
            console.error('Network error executing callback:', callbackId);
            console.error('Error details:', error);
            console.error('Error message:', error.message);
        });
        
        console.log('Fetch request sent for callback:', callbackId);
        
    } catch (e) {
        console.error('Exception in executeCallbackOnServer:', e.message);
        console.error('Exception:', e);
    }
}

// Initialize when DOM is ready
if (document.readyState === 'loading') {
    console.log('Document still loading, waiting for DOMContentLoaded...');
    document.addEventListener('DOMContentLoaded', function() {
        console.log('DOMContentLoaded event fired, starting hydration...');
        startHydration();
    });
} else {
    console.log('Document already loaded, starting hydration immediately...');
    startHydration();
}