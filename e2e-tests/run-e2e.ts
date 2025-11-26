import { execSync, spawn } from 'child_process';
import * as fs from 'fs';
import * as path from 'path';
// @ts-ignore
import waitOn from 'wait-on';

const ROOT_DIR = path.resolve(__dirname, '..');
const CLI_PATH = path.join(ROOT_DIR, 'summon-cli/build/bin/summon');
const TEMP_DIR = path.join(__dirname, 'temp');

// Ensure CLI is built
if (!fs.existsSync(CLI_PATH)) {
    console.error(`CLI not found at ${CLI_PATH}. Please build it first.`);
    process.exit(1);
}

// Ensure temp dir exists
if (fs.existsSync(TEMP_DIR)) {
    fs.rmSync(TEMP_DIR, { recursive: true, force: true });
}
fs.mkdirSync(TEMP_DIR);

interface ProjectConfig {
    name: string;
    type: 'generated' | 'example';
    generatorType?: string; // for generated
    path?: string; // for example
    command: string;
    readyUrl: string;
    testFile?: string; // specific test file to run (defaults to smoke.spec.ts)
}

const projects: ProjectConfig[] = [
    // Generated Projects
    { 
        name: 'gen-js', 
        type: 'generated', 
        generatorType: 'js',
        command: './gradlew jsBrowserDevelopmentRun',
        readyUrl: 'http://localhost:8080'
    },
    // Examples - Hydration Test (comprehensive component testing)
    {
        name: 'hydration-test',
        type: 'example',
        path: '.',
        command: './gradlew :examples:hydration-test:jsBrowserDevelopmentRun',
        readyUrl: 'http://localhost:8080',
        testFile: 'tests/hydration.spec.ts'
    },
    // Examples - Hello World JS
    {
        name: 'hello-world-js',
        type: 'example',
        path: '.',
        command: './gradlew :examples:hello-world-js:jsBrowserDevelopmentRun',
        readyUrl: 'http://localhost:8080',
        testFile: 'tests/smoke.spec.ts'
    }
];

async function run() {
    // Kill anything on port 8080
    try {
        console.log('Cleaning up port 8080...');
        execSync('lsof -ti:8080 | xargs kill -9', { stdio: 'ignore' });
    } catch (e) {
        // Ignore if nothing to kill
    }

    for (const project of projects) {
        console.log(`\n==================================================`);
        console.log(`Testing project: ${project.name}`);
        console.log(`==================================================\n`);
        
        let projectDir: string;

        if (project.type === 'generated') {
            // 1. Generate
            projectDir = path.join(TEMP_DIR, project.name);
            
            let args = `init ${project.name} --package com.example.${project.name.replace(/-/g, '')} --dir ${projectDir} --force`;
            if (project.generatorType === 'js') {
                args += ` --mode standalone`;
            } else {
                // Assume fullstack for others for now, or extend logic
                args += ` --mode fullstack --backend ${project.generatorType}`;
            }

            const generateCmd = `${CLI_PATH} ${args}`;
            console.log(`Generating: ${generateCmd}`);
            
            // Set env var for local build
            process.env.SUMMON_DEV_INCLUDE_BUILD = ROOT_DIR;
            
            try {
                execSync(generateCmd, { stdio: 'inherit', env: process.env });
            } catch (e) {
                console.error('Generation failed');
                process.exit(1);
            }

            // Explicit build to ensure compilation works
            console.log('Building project...');
            try {
                execSync('./gradlew build', { cwd: projectDir, stdio: 'inherit', env: process.env });
            } catch (e) {
                console.error('Build failed');
                process.exit(1);
            }

        } else {
            // Example
            projectDir = path.join(ROOT_DIR, project.path!);
            console.log(`Using existing example at ${projectDir}`);
        }
        
        // 2. Build & Run
        console.log(`Starting project...`);
        const child = spawn(project.command.split(' ')[0], project.command.split(' ').slice(1), {
            cwd: projectDir,
            env: process.env,
            shell: true,
            detached: true // Allow killing the process group
        });
        
        child.stdout.on('data', (data) => {
            process.stdout.write(`[${project.name}] ${data}`);
        });
        
        child.stderr.on('data', (data) => {
            process.stderr.write(`[${project.name}] ${data}`);
        });

        try {
            console.log(`Waiting for ${project.readyUrl}...`);
            const start = Date.now();
            const timeout = 300000; // 5 mins
            let ready = false;
            while (Date.now() - start < timeout) {
                try {
                    await waitOn({
                        resources: [project.readyUrl],
                        timeout: 5000, // check every 5s
                        interval: 1000,
                        validateStatus: function (status: number) {
                            return status >= 200 && status < 400;
                        },
                    });
                    ready = true;
                    console.log('Server is ready!');
                    break;
                } catch (e) {
                    const elapsed = Math.round((Date.now() - start) / 1000);
                    console.log(`Still waiting... (${elapsed}s elapsed)`);
                }
            }
            
            if (!ready) {
                throw new Error(`Timeout waiting for ${project.readyUrl}`);
            }

            // 3. Run Playwright
            const testFile = project.testFile || 'tests/smoke.spec.ts';
            console.log(`Running Playwright tests: ${testFile}...`);
            execSync(`npx playwright test ${testFile}`, { 
                stdio: 'inherit',
                cwd: __dirname,
                env: { ...process.env, BASE_URL: project.readyUrl }
            });
            console.log('Tests passed!');

        } catch (e) {
            console.error('Test failed:', e);
            process.exitCode = 1;
        } finally {
            // 4. Cleanup
            console.log('Stopping server...');
            if (child.pid) {
                try {
                    process.kill(-child.pid); 
                } catch (e) {
                    console.error('Error killing process:', e);
                }
            }

            // Wait for port to be free
            console.log('Waiting for port 8080 to close...');
            const closeStart = Date.now();
            while (Date.now() - closeStart < 30000) {
                try {
                    execSync('lsof -ti:8080', { stdio: 'ignore' });
                    // If lsof succeeds, port is still open
                    await new Promise(r => setTimeout(r, 1000));
                } catch (e) {
                    // lsof failed, port is closed
                    console.log('Port 8080 closed.');
                    break;
                }
            }
        }
    }
}

run();
