# Jakarta EE Auto-Shutdown + On-Demand Startup Plan

## Goal
Run the WareWise Jakarta EE server on a Raspberry Pi so that it:

- Starts only when needed (triggered from portfolio site)
- Auto-shuts down after 1 hour of inactivity
- Conserves Pi resources while remaining accessible on-demand

## 1. On-Demand Startup (from Portfolio Site)

- Portfolio includes a button:  
  “Start WareWise Project →”
- Clicking it sends a request to a local lightweight API (e.g., Flask, Node.js)
- That API runs:  
  ```bash
  systemctl start warewise
  ```

Requires the Jakarta EE app to be set up as a `systemd` service.

## 2. Auto-Shutdown Logic (Inside Jakarta EE App)

- Track last request time:
  - Add a `Filter` that updates a static `lastAccessTime` on every request

- Background watchdog:
  - Create a `@Singleton @Startup` bean
  - Launch a thread that:
    - Checks `lastAccessTime` every minute
    - If idle for ≥ 1 hour, calls `System.exit(0)`

Jakarta EE will shut itself down gracefully.  
You don’t need external cron jobs or scripts to monitor it.

## 3. Restarting the Server Again

- The portfolio site:
  - Checks status via `/api/status`
  - If down, shows a “Starting...” message
  - Sends a POST to `/api/start` (calls `systemctl start warewise`)
  - Redirects to WareWise app when ready

## 4. Optional Enhancements

- Log shutdown times to file for auditing
- Add `/alive` or `/ping` endpoint for health checks
- Prevent shutdown if a user is logged in (check session count)

## Final Outcome

| State       | Behavior                              |
|-------------|----------------------------------------|
| Idle        | WareWise shuts down after 1 hour       |
| User clicks | Portfolio triggers system to start app |
| Running     | Handles requests normally              |