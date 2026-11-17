import React, { useEffect, useState } from "react";
import api from "../api/axiosInstance";

function AdminDashboard({ onBack, onLogout }) {
  // =========================================
  // DASHBOARD STATISTICS
  // =========================================

  const [stats, setStats] = useState({
    totalUsers: 0,
    totalVendors: 0,
    pendingVendors: 0,
    totalProducts: 0,
    totalOrders: 0,
    successfulPayments: 0,
    totalRevenue: 0,
  });

  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [activeMenu, setActiveMenu] = useState("Dashboard");

  // =========================================
  // USERS MANAGEMENT STATE
  // =========================================

  const [users, setUsers] = useState([]);
  const [usersLoading, setUsersLoading] = useState(false);
  const [usersError, setUsersError] = useState("");
  const [userSearch, setUserSearch] = useState("");
  const [selectedRole, setSelectedRole] = useState("ALL");
  const [currentPage, setCurrentPage] = useState(1);

  const pageSize = 5;

  // =========================================
  // VENDOR MANAGEMENT STATE
  // =========================================

  const [vendors, setVendors] = useState([]);
  const [vendorsLoading, setVendorsLoading] = useState(false);
  const [vendorsError, setVendorsError] = useState("");
  const [vendorMessage, setVendorMessage] = useState("");
  const [vendorSearch, setVendorSearch] = useState("");
  const [vendorStatus, setVendorStatus] = useState("ALL");
  const [vendorsPage, setVendorsPage] = useState(1);
  const [vendorActionLoading, setVendorActionLoading] = useState(null);
  const vendorPageSize = 5;

  // =========================================
  // INITIAL DASHBOARD LOAD
  // =========================================

  useEffect(() => {
    fetchDashboardStats();
  }, []);

  // =========================================
  // FETCH DASHBOARD STATISTICS
  // =========================================

  const fetchDashboardStats = async () => {
    try {
      setLoading(true);
      setError("");

      const response = await api.get("/admin/dashboard/statistics");

      const dashboardData = response.data?.data;

      if (dashboardData) {
        setStats(dashboardData);
      }
    } catch (err) {
      console.error("Dashboard statistics error:", err);

      if (err.response?.status === 401) {
        setError("Session expired. Please login again.");
      } else if (err.response?.status === 403) {
        setError("Access denied. Admin privileges required.");
      } else {
        setError("Unable to load dashboard statistics.");
      }
    } finally {
      setLoading(false);
    }
  };

  // =========================================
  // FETCH USERS
  // =========================================

  const fetchUsers = async () => {
    try {
      setUsersLoading(true);
      setUsersError("");

      const response = await api.get("/admin/users");

      setUsers(response.data?.data || []);
    } catch (err) {
      console.error("Users API error:", err);

      if (err.response?.status === 401) {
        setUsersError("Session expired. Please login again.");
      } else if (err.response?.status === 403) {
        setUsersError("Access denied. Admin privileges required.");
      } else {
        setUsersError("Unable to load registered users.");
      }
    } finally {
      setUsersLoading(false);
    }
  };

  // Fetch users whenever Users menu is selected
  useEffect(() => {
    if (activeMenu === "Users") {
      fetchUsers();
    }
  }, [activeMenu]);

  // =========================================
  // FETCH VENDORS
  // =========================================

  const fetchVendors = async () => {
    try {
      setVendorsLoading(true);
      setVendorsError("");
      setVendorMessage("");
      const response = await api.get("/vendors");
      const payload = response.data?.data;
      const list = Array.isArray(payload)
        ? payload
        : Array.isArray(payload?.content)
          ? payload.content
          : Array.isArray(payload?.vendors)
            ? payload.vendors
            : [];
      setVendors(list);
    } catch (err) {
      console.error("Vendors API error:", err);
      if (err.response?.status === 401) {
        setVendorsError("Session expired. Please login again.");
      } else if (err.response?.status === 403) {
        setVendorsError("Access denied. Admin privileges required.");
      } else {
        setVendorsError(err.response?.data?.message || "Unable to load vendors.");
      }
    } finally {
      setVendorsLoading(false);
    }
  };

  useEffect(() => {
    if (activeMenu === "Vendors") fetchVendors();
  }, [activeMenu]);

  const getVendorStatus = (vendor) =>
    String(vendor.status || vendor.vendorStatus || "PENDING").toUpperCase();
  const getVendorId = (vendor) => vendor.id ?? vendor.vendorId;
  const getVendorName = (vendor) =>
    vendor.businessName || vendor.storeName || vendor.vendorName || vendor.name || vendor.userName || "Vendor";
  const getVendorEmail = (vendor) =>
    vendor.email || vendor.userEmail || vendor.user?.email || "—";

  const filteredVendors = vendors.filter((vendor) => {
    const search = `${getVendorId(vendor)} ${getVendorName(vendor)} ${getVendorEmail(vendor)} ${vendor.businessName || ""} ${getVendorStatus(vendor)}`.toLowerCase();
    return search.includes(vendorSearch.trim().toLowerCase()) &&
      (vendorStatus === "ALL" || getVendorStatus(vendor) === vendorStatus);
  });
  const vendorsTotalPages = Math.max(1, Math.ceil(filteredVendors.length / vendorPageSize));
  const vendorsStartIndex = (vendorsPage - 1) * vendorPageSize;
  const paginatedVendors = filteredVendors.slice(vendorsStartIndex, vendorsStartIndex + vendorPageSize);

  useEffect(() => { setVendorsPage(1); }, [vendorSearch, vendorStatus]);

  const handleVendorAction = async (vendor, action) => {
    const id = getVendorId(vendor);
    if (id == null) {
      setVendorsError("Vendor ID is missing; cannot perform this action.");
      return;
    }
    const labels = { approve: "approve", reject: "reject", suspend: "suspend" };
    if (!window.confirm(`Are you sure you want to ${labels[action]} ${getVendorName(vendor)}?`)) return;
    const endpoint = `/vendors/admin/${id}/${action}`;
    try {
      setVendorActionLoading(id);
      setVendorsError("");
      setVendorMessage("");
      await api.put(endpoint);
      setVendorMessage(`Vendor ${getVendorName(vendor)} ${action === "approve" ? "approved" : action === "reject" ? "rejected" : "suspended"} successfully.`);
      await fetchVendors();
      await fetchDashboardStats();
    } catch (err) {
      console.error(`Vendor ${action} error:`, err);
      setVendorsError(err.response?.data?.message || `Unable to ${action} vendor. Please check the backend response.`);
    } finally {
      setVendorActionLoading(null);
    }
  };

  // =========================================
  // SEARCH + ROLE FILTER
  // =========================================

  const filteredUsers = users.filter((user) => {
    const searchText =
      `${user.id} ${user.name} ${user.email} ${user.role}`.toLowerCase();

    const matchesSearch = searchText.includes(userSearch.trim().toLowerCase());

    const matchesRole =
      selectedRole === "ALL" ||
      String(user.role).toUpperCase() === selectedRole;

    return matchesSearch && matchesRole;
  });

  // =========================================
  // PAGINATION
  // =========================================

  const totalPages = Math.max(1, Math.ceil(filteredUsers.length / pageSize));

  const startIndex = (currentPage - 1) * pageSize;

  const paginatedUsers = filteredUsers.slice(startIndex, startIndex + pageSize);

  // Reset page when search or role changes
  useEffect(() => {
    setCurrentPage(1);
  }, [userSearch, selectedRole]);

  // =========================================
  // CURRENCY FORMATTER
  // =========================================

  const formatCurrency = (amount) => {
    return new Intl.NumberFormat("en-IN", {
      style: "currency",
      currency: "INR",
      maximumFractionDigits: 2,
    }).format(amount || 0);
  };

  // =========================================
  // DASHBOARD CARDS
  // =========================================

  const dashboardCards = [
    {
      title: "Total Users",
      value: stats.totalUsers,
      icon: "👥",
      color: "#6366f1",
    },
    {
      title: "Total Vendors",
      value: stats.totalVendors,
      icon: "🏪",
      color: "#0891b2",
    },
    {
      title: "Pending Vendors",
      value: stats.pendingVendors,
      icon: "⏳",
      color: "#d97706",
    },
    {
      title: "Total Products",
      value: stats.totalProducts,
      icon: "📦",
      color: "#16a34a",
    },
    {
      title: "Total Orders",
      value: stats.totalOrders,
      icon: "🛒",
      color: "#7c3aed",
    },
    {
      title: "Successful Payments",
      value: stats.successfulPayments,
      icon: "💳",
      color: "#059669",
    },
    {
      title: "Total Revenue",
      value: formatCurrency(stats.totalRevenue),
      icon: "💰",
      color: "#ea580c",
    },
  ];

  // =========================================
  // SIDEBAR NAVIGATION
  // =========================================

  const menuItems = [
    { name: "Dashboard", icon: "📊" },
    { name: "Users", icon: "👥" },
    { name: "Vendors", icon: "🏪" },
    { name: "Products", icon: "📦" },
    { name: "Orders", icon: "🛒" },
  ];

  // =========================================
  // MANAGEMENT CONTENT
  // =========================================

  const renderManagementContent = () => {
    switch (activeMenu) {
      // =====================================
      // USERS MANAGEMENT
      // =====================================

      case "Users":
        return (
          <section className="admin-panel">
            <div className="admin-panel-heading">
              <div>
                <h3>Users Management</h3>
                <p>Manage registered customers and administrator accounts.</p>
              </div>

              <button
                className="admin-refresh-btn"
                onClick={fetchUsers}
                disabled={usersLoading}
              >
                {usersLoading ? "Loading..." : "↻ Refresh Users"}
              </button>
            </div>

            {/* SEARCH + ROLE FILTER */}

            <div className="admin-users-toolbar">
              <div className="admin-management-stat">
                <span>Filtered Users</span>
                <strong>{filteredUsers.length}</strong>
              </div>

              <input
                className="admin-users-search"
                type="text"
                placeholder="Search by ID, name, email..."
                value={userSearch}
                onChange={(e) => setUserSearch(e.target.value)}
              />

              <select
                className="admin-users-role-filter"
                value={selectedRole}
                onChange={(e) => setSelectedRole(e.target.value)}
              >
                <option value="ALL">All Roles</option>
                <option value="CUSTOMER">Customer</option>
                <option value="VENDOR">Vendor</option>
                <option value="ADMIN">Admin</option>
              </select>
            </div>

            {/* USERS ERROR */}

            {usersError && (
              <div className="admin-error">
                <p>{usersError}</p>
                <button onClick={fetchUsers}>Retry</button>
              </div>
            )}

            {/* USERS TABLE */}

            {usersLoading ? (
              <div className="admin-loading">Loading registered users...</div>
            ) : (
              <>
                <div className="admin-table-wrapper">
                  <table className="admin-table">
                    <thead>
                      <tr>
                        <th>ID</th>
                        <th>Name</th>
                        <th>Email</th>
                        <th>Role</th>
                      </tr>
                    </thead>

                    <tbody>
                      {paginatedUsers.length > 0 ? (
                        paginatedUsers.map((user) => {
                          const role = String(user.role).toUpperCase();

                          return (
                            <tr key={user.id}>
                              <td>{user.id}</td>
                              <td>{user.name}</td>
                              <td>{user.email}</td>

                              <td>
                                <span
                                  className={`admin-role-badge ${
                                    role === "ADMIN"
                                      ? "admin-role"
                                      : role === "VENDOR"
                                        ? "vendor-role"
                                        : "customer-role"
                                  }`}
                                >
                                  {role}
                                </span>
                              </td>
                            </tr>
                          );
                        })
                      ) : (
                        <tr>
                          <td colSpan="4" className="admin-empty-state">
                            {users.length === 0
                              ? "No registered users found."
                              : "No users match your search or selected role."}
                          </td>
                        </tr>
                      )}
                    </tbody>
                  </table>
                </div>

                {/* PAGINATION */}

                <div className="admin-pagination">
                  <span className="admin-pagination-info">
                    Showing {filteredUsers.length === 0 ? 0 : startIndex + 1}
                    {" - "}
                    {Math.min(startIndex + pageSize, filteredUsers.length)}
                    {" of "}
                    {filteredUsers.length} users
                  </span>

                  <div className="admin-pagination-buttons">
                    <button
                      className="admin-page-btn"
                      disabled={currentPage === 1}
                      onClick={() => setCurrentPage((prev) => prev - 1)}
                    >
                      ← Previous
                    </button>

                    <span className="admin-page-number">
                      Page {currentPage} / {totalPages}
                    </span>

                    <button
                      className="admin-page-btn"
                      disabled={currentPage >= totalPages}
                      onClick={() => setCurrentPage((prev) => prev + 1)}
                    >
                      Next →
                    </button>
                  </div>
                </div>
              </>
            )}
          </section>
        );

      // =====================================
      // VENDORS MANAGEMENT
      // =====================================

      case "Vendors":
        return (
          <section className="admin-panel">
            <div className="admin-panel-heading">
              <div>
                <h3>Vendors Management</h3>
                <p>Review applications and manage marketplace vendor accounts.</p>
              </div>
              <button className="admin-refresh-btn" onClick={fetchVendors} disabled={vendorsLoading}>
                {vendorsLoading ? "Loading..." : "↻ Refresh Vendors"}
              </button>
            </div>

            <div className="admin-vendor-toolbar">
              <div className="admin-management-stat">
                <span>Matching Vendors</span><strong>{filteredVendors.length}</strong>
              </div>
              <input className="admin-users-search" type="text"
                placeholder="Search ID, vendor, business or email..."
                value={vendorSearch} onChange={(e) => setVendorSearch(e.target.value)} />
              <select className="admin-users-role-filter" value={vendorStatus}
                onChange={(e) => setVendorStatus(e.target.value)}>
                <option value="ALL">All Statuses</option>
                <option value="PENDING">Pending</option>
                <option value="APPROVED">Approved</option>
                <option value="REJECTED">Rejected</option>
                <option value="SUSPENDED">Suspended</option>
              </select>
            </div>

            <div className="admin-vendor-summary">
              <span>Total: <strong>{vendors.length}</strong></span>
              <span>Pending: <strong>{vendors.filter(v => getVendorStatus(v) === "PENDING").length}</strong></span>
              <span>Approved: <strong>{vendors.filter(v => getVendorStatus(v) === "APPROVED").length}</strong></span>
            </div>

            {vendorMessage && <div className="admin-success">{vendorMessage}</div>}
            {vendorsError && <div className="admin-error"><p>{vendorsError}</p><button onClick={fetchVendors}>Retry</button></div>}

            {vendorsLoading ? <div className="admin-loading">Loading vendors...</div> : (
              <>
                <div className="admin-table-wrapper">
                  <table className="admin-table admin-vendor-table">
                    <thead><tr><th>ID</th><th>Vendor / Business</th><th>Email</th><th>Status</th><th>Actions</th></tr></thead>
                    <tbody>
                      {paginatedVendors.length ? paginatedVendors.map((vendor) => {
                        const id = getVendorId(vendor);
                        const status = getVendorStatus(vendor);
                        const busy = vendorActionLoading === id;
                        return <tr key={id ?? `${getVendorName(vendor)}-${getVendorEmail(vendor)}`}>
                          <td>{id ?? "—"}</td>
                          <td><strong>{getVendorName(vendor)}</strong>{vendor.businessName && vendor.businessName !== getVendorName(vendor) && <small className="admin-vendor-subtext">{vendor.businessName}</small>}</td>
                          <td>{getVendorEmail(vendor)}</td>
                          <td><span className={`admin-vendor-status vendor-status-${status.toLowerCase()}`}>{status}</span></td>
                          <td><div className="admin-vendor-actions">
                            {status === "PENDING" && <>
                              <button className="admin-vendor-action-btn approve" disabled={busy} onClick={() => handleVendorAction(vendor,"approve")}>{busy ? "Working…" : "Approve"}</button>
                              <button className="admin-vendor-action-btn reject" disabled={busy} onClick={() => handleVendorAction(vendor,"reject")}>Reject</button>
                            </>}
                            {status === "APPROVED" && <button className="admin-vendor-action-btn suspend" disabled={busy} onClick={() => handleVendorAction(vendor,"suspend")}>{busy ? "Working…" : "Suspend"}</button>}
                            {status !== "PENDING" && status !== "APPROVED" && <span className="admin-vendor-no-action">No actions</span>}
                          </div></td>
                        </tr>;
                      }) : <tr><td colSpan="5" className="admin-empty-state">{vendors.length ? "No vendors match your filters." : "No vendors found."}</td></tr>}
                    </tbody>
                  </table>
                </div>
                <div className="admin-pagination">
                  <span className="admin-pagination-info">Showing {filteredVendors.length ? vendorsStartIndex + 1 : 0} - {Math.min(vendorsStartIndex + vendorPageSize, filteredVendors.length)} of {filteredVendors.length} vendors</span>
                  <div className="admin-pagination-buttons">
                    <button className="admin-page-btn" disabled={vendorsPage === 1} onClick={() => setVendorsPage(p => p - 1)}>← Previous</button>
                    <span className="admin-page-number">Page {vendorsPage} / {vendorsTotalPages}</span>
                    <button className="admin-page-btn" disabled={vendorsPage >= vendorsTotalPages} onClick={() => setVendorsPage(p => p + 1)}>Next →</button>
                  </div>
                </div>
              </>
            )}
          </section>
        );

      // =====================================
      // PRODUCTS MANAGEMENT
      // =====================================

      case "Products":
        return (
          <section className="admin-panel">
            <div className="admin-panel-heading">
              <h3>Products Management</h3>
            </div>

            <div className="admin-management-card">
              <div className="admin-management-icon">📦</div>

              <h3>Marketplace Products</h3>

              <p>
                Monitor products listed by vendors across the MarketSphere
                marketplace.
              </p>

              <div className="admin-management-stat">
                <span>Total Products</span>
                <strong>{stats.totalProducts}</strong>
              </div>

              <button
                className="admin-action-btn"
                onClick={() => setActiveMenu("Dashboard")}
              >
                Back to Dashboard
              </button>
            </div>
          </section>
        );

      // =====================================
      // ORDERS MANAGEMENT
      // =====================================

      case "Orders":
        return (
          <section className="admin-panel">
            <div className="admin-panel-heading">
              <h3>Orders Management</h3>
            </div>

            <div className="admin-management-card">
              <div className="admin-management-icon">🛒</div>

              <h3>Marketplace Orders</h3>

              <p>Monitor customer orders and marketplace transactions.</p>

              <div className="admin-management-stats">
                <div>
                  <span>Total Orders</span>
                  <strong>{stats.totalOrders}</strong>
                </div>

                <div>
                  <span>Successful Payments</span>
                  <strong>{stats.successfulPayments}</strong>
                </div>
              </div>

              <button
                className="admin-action-btn"
                onClick={() => setActiveMenu("Dashboard")}
              >
                Back to Dashboard
              </button>
            </div>
          </section>
        );

      default:
        return null;
    }
  };

  // =========================================
  // MAIN UI
  // =========================================

  return (
    <div className="admin-layout">
      {/* SIDEBAR */}

      <aside className="admin-sidebar">
        <div className="admin-brand">
          <h2>MarketSphere</h2>
          <span>Admin Panel</span>
        </div>

        <nav className="admin-nav">
          {menuItems.map((item) => (
            <button
              key={item.name}
              className={`admin-nav-item ${
                activeMenu === item.name ? "active" : ""
              }`}
              onClick={() => setActiveMenu(item.name)}
            >
              <span>{item.icon}</span>
              {item.name}
            </button>
          ))}
        </nav>

        {/* SIDEBAR FOOTER */}

        <div className="admin-sidebar-footer">
          <button className="admin-nav-item" onClick={onBack}>
            <span>🏠</span>
            Back to Store
          </button>

          <button className="admin-nav-item" onClick={onLogout}>
            <span>🚪</span>
            Logout
          </button>
        </div>
      </aside>

      {/* MAIN CONTENT */}

      <main className="admin-content">
        <header className="admin-header">
          <div>
            <h2>
              {activeMenu === "Dashboard" ? "Admin Dashboard" : activeMenu}
            </h2>

            <p>Welcome to MarketSphere Administration</p>
          </div>

          <button
            className="admin-refresh-btn"
            onClick={fetchDashboardStats}
            disabled={loading}
          >
            {loading ? "Refreshing..." : "↻ Refresh"}
          </button>
        </header>

        {/* DASHBOARD ERROR */}

        {error && activeMenu === "Dashboard" && (
          <div className="admin-error">
            <p>{error}</p>
            <button onClick={fetchDashboardStats}>Retry</button>
          </div>
        )}

        {/* DASHBOARD */}

        {activeMenu === "Dashboard" ? (
          <>
            <section className="admin-page-heading">
              <h3>Overview</h3>
              <p>Monitor your marketplace performance.</p>
            </section>

            {loading ? (
              <div className="admin-loading">
                Loading dashboard statistics...
              </div>
            ) : (
              <section className="admin-stats-grid">
                {dashboardCards.map((card) => (
                  <div className="admin-stat-card" key={card.title}>
                    <div className="admin-stat-top">
                      <div>
                        <p>{card.title}</p>
                        <h2>{card.value}</h2>
                      </div>

                      <div
                        className="admin-stat-icon"
                        style={{ backgroundColor: card.color }}
                      >
                        {card.icon}
                      </div>
                    </div>
                  </div>
                ))}
              </section>
            )}
          </>
        ) : (
          renderManagementContent()
        )}
      </main>
    </div>
  );
}

export default AdminDashboard;
