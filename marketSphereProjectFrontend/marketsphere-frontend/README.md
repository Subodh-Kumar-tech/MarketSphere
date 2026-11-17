# MarketSphere Frontend

## Run
1. Start your Spring Boot backend (default API URL: `http://localhost:1610/api`).
2. Open this folder in terminal.
3. Run `npm install`
4. Run `npm run dev`

Set `VITE_API_BASE_URL` in `.env` if backend base URL differs. Axios automatically sends `Authorization: Bearer <token>` from localStorage key `token`.

## Included
- Storefront: product list/search/category, wishlist, local cart, registration/login.
- Admin dashboard component integrated from the provided AdminDashboard source; Admin entry appears after login when role is `ADMIN`.
- Vendor portal: vendor status/store lookup and vendor registration using `/vendors/register/{userId}`, `/vendors/status/{userId}`, `/vendors/my-store/{userId}`, and product list by vendor.

## Notes
The storefront cart and checkout remain browser-local/UI-only; this package does not pretend to implement server cart, payment, or order APIs where exact request contracts are not included in the frontend source. Admin dashboard uses `/admin/dashboard/statistics` and `/admin/users` as in the supplied dashboard source.
