// Theme management functions
document.addEventListener('DOMContentLoaded', function() {
    // Initialize theme
    applyTheme('light');
    
    // Add event listeners to theme option elements
    const themeOptions = document.querySelectorAll('.theme-option');
    themeOptions.forEach(option => {
        option.addEventListener('click', function() {
            const themeName = this.getAttribute('data-theme');
            applyTheme(themeName);
            
            // Update active state
            themeOptions.forEach(opt => opt.classList.remove('active'));
            this.classList.add('active');
            
            // Update current theme name display
            document.getElementById('current-theme-name').textContent = themeName.charAt(0).toUpperCase() + themeName.slice(1);
        });
    });
    
    // Add event listeners to color inputs for custom theme
    const colorInputs = document.querySelectorAll('input[type="color"]');
    colorInputs.forEach(input => {
        input.addEventListener('input', function() {
            document.getElementById(this.id + '-value').textContent = this.value;
            updateCustomThemePreview();
        });
    });
    
    // Add event listener to font size range input
    const fontSizeInput = document.getElementById('font-size');
    if (fontSizeInput) {
        fontSizeInput.addEventListener('input', function() {
            document.getElementById('font-size-value').textContent = this.value + 'px';
            updateCustomThemePreview();
        });
    }
});

// Apply theme to the document
function applyTheme(themeName) {
    const root = document.documentElement;
    
    // Remove all theme classes from body
    document.body.classList.remove('theme-light', 'theme-dark', 'theme-forest', 'theme-ocean');
    
    // Add the selected theme class
    document.body.classList.add('theme-' + themeName);
    
    // Set CSS variables based on theme
    switch(themeName) {
        case 'light':
            root.style.setProperty('--primary-color', '#4695EB');
            root.style.setProperty('--text-color', '#333333');
            root.style.setProperty('--bg-color', '#FFFFFF');
            root.style.setProperty('--secondary-color', '#FF4081');
            break;
        case 'dark':
            root.style.setProperty('--primary-color', '#BB86FC');
            root.style.setProperty('--text-color', '#E1E1E1');
            root.style.setProperty('--bg-color', '#121212');
            root.style.setProperty('--secondary-color', '#03DAC6');
            break;
        case 'forest':
            root.style.setProperty('--primary-color', '#4CAF50');
            root.style.setProperty('--text-color', '#212121');
            root.style.setProperty('--bg-color', '#F1F8E9');
            root.style.setProperty('--secondary-color', '#FF9800');
            break;
        case 'ocean':
            root.style.setProperty('--primary-color', '#039BE5');
            root.style.setProperty('--text-color', '#01579B');
            root.style.setProperty('--bg-color', '#E1F5FE');
            root.style.setProperty('--secondary-color', '#FF3D00');
            break;
    }
    
    console.log('Theme applied:', themeName);
}

// Update the custom theme preview
function updateCustomThemePreview() {
    const primaryColor = document.getElementById('primary-color').value;
    const textColor = document.getElementById('text-color').value;
    const backgroundColor = document.getElementById('background-color').value;
    const fontSize = document.getElementById('font-size')?.value || '16';
    
    const preview = document.getElementById('theme-preview');
    if (preview) {
        preview.style.backgroundColor = backgroundColor;
        preview.style.color = textColor;
        preview.style.fontSize = fontSize + 'px';
        
        const heading = preview.querySelector('h3');
        if (heading) heading.style.color = primaryColor;
        
        const buttons = preview.querySelectorAll('button');
        if (buttons.length > 0) {
            buttons[0].style.backgroundColor = primaryColor;
        }
    }
}

// WebSocket chat functionality improvements
document.addEventListener('DOMContentLoaded', function() {
    const joinChatBtn = document.getElementById('join-chat-btn');
    if (joinChatBtn) {
        joinChatBtn.addEventListener('click', function() {
            console.log('Join chat button clicked');
        });
    }
    
    // Add logging for WebSocket events
    window.addEventListener('beforeunload', function() {
        if (window.socket && socket.readyState === WebSocket.OPEN) {
            socket.close();
            console.log('WebSocket closed on page unload');
        }
    });
}); 