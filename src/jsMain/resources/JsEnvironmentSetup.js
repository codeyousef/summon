// JavaScript implementation for environment-specific event setup

// Animation Controller implementation
function setupAnimationController() {
    if (typeof window !== 'undefined') {
        // Create the animation controller object
        window.animationController = {
            // Animation state
            _status: 'idle', // idle, running, paused, stopped
            _progress: 0,     // 0.0 to 1.0
            _animationId: null,
            _startTime: 0,
            _pauseTime: 0,
            _duration: 300,   // Default duration in ms

            // Methods
            pause: function() {
                if (this._status === 'running') {
                    this._pauseTime = performance.now();
                    this._status = 'paused';
                    if (this._animationId) {
                        cancelAnimationFrame(this._animationId);
                        this._animationId = null;
                    }
                }
            },

            resume: function() {
                if (this._status === 'paused') {
                    // Adjust start time to account for pause duration
                    const pauseDuration = performance.now() - this._pauseTime;
                    this._startTime += pauseDuration;
                    this._status = 'running';
                    this._animate();
                }
            },

            cancel: function() {
                this._stopAnimation();
                this._progress = 0;
                this._status = 'idle';
            },

            stop: function() {
                this._stopAnimation();
                this._status = 'stopped';
            },

            // Start a new animation
            start: function(durationMs = 300) {
                this._stopAnimation();
                this._duration = durationMs;
                this._startTime = performance.now();
                this._progress = 0;
                this._status = 'running';
                this._animate();
            },

            // Private method to handle animation frame updates
            _animate: function() {
                const self = this;
                this._animationId = requestAnimationFrame(function(timestamp) {
                    if (self._status === 'running') {
                        const elapsedTime = timestamp - self._startTime;

                        if (elapsedTime >= self._duration) {
                            // Animation complete
                            self._progress = 1;
                            self._status = 'stopped';
                            self._animationId = null;
                        } else {
                            // Update progress
                            self._progress = Math.min(elapsedTime / self._duration, 1);
                            self._animate(); // Continue animation
                        }
                    }
                });
            },

            // Private method to stop animation
            _stopAnimation: function() {
                if (this._animationId) {
                    cancelAnimationFrame(this._animationId);
                    this._animationId = null;
                }
            },

            // Properties (getters)
            get status() {
                return this._status;
            },

            get progress() {
                return this._progress;
            }
        };

        console.log("Animation controller initialized");
    }
}

// Browser environment setup
function setupBrowserEvents(lifecycleOwner) {
    // Initialize animation controller
    setupAnimationController();

    if (typeof document !== 'undefined') {
        document.addEventListener('visibilitychange', function() {
            if (document.visibilityState === 'visible') {
                // Application becoming visible
                lifecycleOwner.notifyVisible();
            } else {
                // Application being hidden
                lifecycleOwner.notifyHidden();
            }
        });
    }

    if (typeof window !== 'undefined') {
        window.addEventListener('beforeunload', function() {
            // Application being unloaded
            lifecycleOwner.notifyStopping();
        });
    }
}

// Node.js environment setup
function setupNodeEvents(lifecycleOwner) {
    if (typeof process !== 'undefined') {
        process.on('exit', function() {
            lifecycleOwner.notifyDestroyed();
        });

        process.on('SIGINT', function() {
            lifecycleOwner.notifyStopping();
        });

        process.on('SIGTERM', function() {
            lifecycleOwner.notifyStopping();
        });
    }
}

// Web Worker environment setup
function setupWebWorkerEvents(lifecycleOwner) {
    if (typeof self !== 'undefined' && typeof self.addEventListener === 'function') {
        self.addEventListener('message', function(e) {
            // Handle custom lifecycle messages
            if (e.data && e.data.type === 'lifecycle') {
                if (e.data.action === 'pause') {
                    lifecycleOwner.notifyHidden();
                } else if (e.data.action === 'resume') {
                    lifecycleOwner.notifyVisible();
                } else if (e.data.action === 'terminate') {
                    lifecycleOwner.notifyStopping();
                }
            }
        });
    }
}

// Export the functions
module.exports = {
    setupBrowserEvents: setupBrowserEvents,
    setupNodeEvents: setupNodeEvents,
    setupWebWorkerEvents: setupWebWorkerEvents
}; 
