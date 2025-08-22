/**
 * Theme Switcher for Virtual Classroom
 * Allows users to switch between different color themes
 */

document.addEventListener('DOMContentLoaded', function() {
    // Create theme switcher if it doesn't exist
    if (!document.querySelector('.theme-switcher')) {
        createThemeSwitcher();
    }
    
    // Initialize theme from localStorage or default
    initializeTheme();
});

/**
 * Creates the theme switcher UI
 */
function createThemeSwitcher() {
    // Create container
    const themeSwitcher = document.createElement('div');
    themeSwitcher.className = 'theme-switcher';
    
    // Define available themes
    const themes = [
        { name: 'default', label: 'Default Theme' },
        { name: 'ocean', label: 'Ocean Theme' },
        { name: 'forest', label: 'Forest Theme' },
        { name: 'sunset', label: 'Sunset Theme' },
        { name: 'purple', label: 'Purple Theme' },
        { name: 'dark', label: 'Dark Mode' }
    ];
    
    // Create theme options
    themes.forEach(theme => {
        const themeOption = document.createElement('button');
        themeOption.className = `theme-option ${theme.name}`;
        themeOption.setAttribute('data-theme', theme.name);
        themeOption.setAttribute('title', theme.label);
        themeOption.setAttribute('aria-label', `Switch to ${theme.label}`);
        
        themeOption.addEventListener('click', () => {
            setTheme(theme.name);
        });
        
        themeSwitcher.appendChild(themeOption);
    });
    
    // Add theme switcher to the page
    // Try to add it to the navbar first, otherwise add to body
    const navbar = document.querySelector('.landing-navbar') || document.querySelector('nav');
    if (navbar) {
        // Create a container for the theme switcher in the navbar
        const themeContainer = document.createElement('div');
        themeContainer.className = 'theme-switcher-container';
        themeContainer.appendChild(themeSwitcher);
        navbar.appendChild(themeContainer);
        
        // Add some specific styling for navbar placement
        const style = document.createElement('style');
        style.textContent = `
            .theme-switcher-container {
                margin-left: auto;
                padding: 0 1rem;
            }
            @media (max-width: 768px) {
                .theme-switcher-container {
                    position: absolute;
                    top: 70px;
                    right: 20px;
                }
            }
        `;
        document.head.appendChild(style);
    } else {
        // If no navbar, add to top-right corner of body
        themeSwitcher.style.position = 'fixed';
        themeSwitcher.style.top = '20px';
        themeSwitcher.style.right = '20px';
        themeSwitcher.style.zIndex = '1000';
        document.body.appendChild(themeSwitcher);
    }
}

/**
 * Sets the active theme
 * @param {string} themeName - The name of the theme to set
 */
function setTheme(themeName) {
    // Remove all theme classes
    document.body.classList.remove(
        'theme-default',
        'theme-ocean',
        'theme-forest',
        'theme-sunset',
        'theme-purple',
        'theme-dark'
    );
    
    // Add the selected theme class if not default
    if (themeName !== 'default') {
        document.body.classList.add(`theme-${themeName}`);
    }
    
    // Update active state on theme options
    const themeOptions = document.querySelectorAll('.theme-option');
    themeOptions.forEach(option => {
        if (option.getAttribute('data-theme') === themeName) {
            option.classList.add('active');
        } else {
            option.classList.remove('active');
        }
    });
    
    // Apply text visibility classes based on theme
    if (themeName === 'dark') {
        // For dark theme, ensure text is visible on dark backgrounds
        document.querySelectorAll('.navbar-brand, .nav-link, h1, h2, h3, h4, h5, h6, p, label, .card-title, .card-text')
            .forEach(el => {
                // Only add class if the element doesn't already have a text-on-* class
                if (!el.className.includes('text-on-')) {
                    el.classList.add('text-on-dark');
                }
            });
    } else {
        // For light themes, remove dark text classes
        document.querySelectorAll('.text-on-dark').forEach(el => {
            el.classList.remove('text-on-dark');
        });
    }
    
    // Save theme preference
    localStorage.setItem('preferred-theme', themeName);
}

/**
 * Initializes the theme from localStorage or defaults to 'default'
 */
function initializeTheme() {
    const savedTheme = localStorage.getItem('preferred-theme') || 'default';
    setTheme(savedTheme);
}