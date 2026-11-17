import React, { useEffect, useMemo, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import {
  Search,
  ShoppingBag,
  UserRound,
  Heart,
  ArrowRight,
  ChevronDown,
  Menu,
  X,
  Star,
  Truck,
  ShieldCheck,
  RefreshCw,
  Plus,
  Minus,
  Trash2,
  Check,
  Package,
  LogOut,
} from "lucide-react";
import api from "./api/axiosInstance";
import { loginUser, registerUser } from "./api/authService";

import AdminDashboard from "./pages/AdminDashboard.jsx";
import VendorPortal from "./pages/VendorPortal.jsx";

const seed = [
  {
    id: 1,
    name: "Noise Cancelling Wireless Headphones",
    category: "Electronics",
    price: 3499,
    oldPrice: 5999,
    rating: 4.7,
    tag: "BESTSELLER",
    image:
      "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?auto=format&fit=crop&w=700&q=80",
  },
  {
    id: 2,
    name: "Everyday Minimal Sneakers",
    category: "Fashion",
    price: 2299,
    oldPrice: 3999,
    rating: 4.5,
    tag: "TRENDING",
    image:
      "https://images.unsplash.com/photo-1542291026-7eec264c27ff?auto=format&fit=crop&w=700&q=80",
  },
  {
    id: 3,
    name: "Ceramic Pour-over Coffee Set",
    category: "Home",
    price: 1299,
    oldPrice: 1899,
    rating: 4.8,
    tag: "EDITOR'S PICK",
    image:
      "https://images.unsplash.com/photo-1495474472287-4d71bcdd2085?auto=format&fit=crop&w=700&q=80",
  },
  {
    id: 4,
    name: "Smart Fitness Watch — Pro",
    category: "Electronics",
    price: 4999,
    oldPrice: 7999,
    rating: 4.6,
    tag: "NEW",
    image:
      "https://images.unsplash.com/photo-1523275335684-37898b6baf30?auto=format&fit=crop&w=700&q=80",
  },
  {
    id: 5,
    name: "Soft Knit Everyday Tote",
    category: "Fashion",
    price: 899,
    oldPrice: 1499,
    rating: 4.4,
    tag: "POPULAR",
    image:
      "https://images.unsplash.com/photo-1548036328-c9fa89d128fa?auto=format&fit=crop&w=700&q=80",
  },
  {
    id: 6,
    name: "Ambient Table Lamp",
    category: "Home",
    price: 1799,
    oldPrice: 2499,
    rating: 4.7,
    tag: "LIMITED",
    image:
      "https://images.unsplash.com/photo-1507473885765-e6ed057f782c?auto=format&fit=crop&w=700&q=80",
  },
  {
    id: 7,
    name: "Hydration Bottle 1L",
    category: "Lifestyle",
    price: 699,
    oldPrice: 999,
    rating: 4.3,
    tag: "VALUE PICK",
    image:
      "https://images.unsplash.com/photo-1602143407151-7111542de6e8?auto=format&fit=crop&w=700&q=80",
  },
  {
    id: 8,
    name: "Classic Analog Watch",
    category: "Fashion",
    price: 3199,
    oldPrice: 4599,
    rating: 4.6,
    tag: "JUST IN",
    image:
      "https://images.unsplash.com/photo-1524592094714-0f0654e20314?auto=format&fit=crop&w=700&q=80",
  },
];
const money = (n) => "₹" + Number(n).toLocaleString("en-IN");
function App() {
  const [products, setProducts] = useState(seed),
    [cart, setCart] = useState(() =>
      JSON.parse(localStorage.getItem("ms-cart") || "[]"),
    ),
    [wishlist, setWishlist] = useState([]),
    [query, setQuery] = useState(""),
    [category, setCategory] = useState("All"),
    [notice, setNotice] = useState(""),
    [user, setUser] = useState(() =>
      JSON.parse(localStorage.getItem("ms-user") || "null"),
    ),
    [mobile, setMobile] = useState(false),
    [page, setPage] = useState("home"),
    [authMode, setAuthMode] = useState("login");
  const [form, setForm] = useState({
    name: "",
    email: "",
    password: "",
    phone: "",
  });
  const navigate = useNavigate();
  useEffect(() => {
    localStorage.setItem("ms-cart", JSON.stringify(cart));
  }, [cart]);
  useEffect(() => {
    api
      .get("/products")
      .then((r) => {
        const d = r.data?.data ?? r.data;
        const arr = Array.isArray(d) ? d : d?.content || d?.products;
        if (arr?.length)
          setProducts(
            arr.map((p) => ({
              id: p.id,
              name: p.name || p.title,
              category: p.category?.name || p.category || "General",
              price: p.price ?? p.sellingPrice ?? 0,
              oldPrice: p.mrp ?? p.price,
              rating: p.rating || 4.4,
              image: p.imageUrl || p.image || seed[p.id % seed.length].image,
              tag: p.isFeatured ? "FEATURED" : "",
            })),
          );
      })
      .catch(() => {});
  }, []);
  const filtered = useMemo(
    () =>
      products.filter(
        (p) =>
          (category === "All" ||
            String(p.category).toLowerCase() === category.toLowerCase()) &&
          p.name.toLowerCase().includes(query.toLowerCase()),
      ),
    [products, category, query],
  );
  const count = cart.reduce((s, i) => s + i.qty, 0),
    total = cart.reduce((s, i) => s + i.price * i.qty, 0);
  const toast = (t) => {
    setNotice(t);
    setTimeout(() => setNotice(""), 2300);
  };
  const add = (p) => {
    setCart((c) => {
      const old = c.find((i) => i.id === p.id);
      return old
        ? c.map((i) => (i.id === p.id ? { ...i, qty: i.qty + 1 } : i))
        : [...c, { ...p, qty: 1 }];
    });
    toast("Added to your bag");
  };
  const qty = (id, n) =>
    setCart((c) =>
      c
        .map((i) => (i.id === id ? { ...i, qty: i.qty + n } : i))
        .filter((i) => i.qty > 0),
    );
  const toggleWish = (p) =>
    setWishlist((w) =>
      w.includes(p.id) ? w.filter((id) => id !== p.id) : [...w, p.id],
    );
  const submitAuth = async (e) => {
    e.preventDefault();
    try {
      if (authMode === "login") {
        const r = await loginUser({
          email: form.email,
          password: form.password,
        });
        const d = r.data?.data ?? r.data;
        localStorage.setItem("token", d.token);
        const u = { name: d.name, email: d.email, role: d.role };
        localStorage.setItem("ms-user", JSON.stringify(u));
        setUser(u);
        toast("Welcome back!");
        setPage("home");
      } else {
        await registerUser(form);
        toast("Account created — please sign in");
        setAuthMode("login");
      }
    } catch (err) {
      toast(
        err.response?.data?.message ||
          "Could not complete request. Check backend/API.",
      );
    }
  };
  const categories = ["All", "Electronics", "Fashion", "Home", "Lifestyle"];
  const ProductCard = ({ p }) => (
    <article className="product-card">
      <div className="product-image">
        <img src={p.image} alt={p.name} loading="lazy" />
        {p.tag && <span className="product-tag">{p.tag}</span>}
        <button
          className={"wish " + (wishlist.includes(p.id) ? "active" : "")}
          onClick={() => toggleWish(p)}
          aria-label="Add to wishlist"
        >
          <Heart
            size={17}
            fill={wishlist.includes(p.id) ? "currentColor" : "none"}
          />
        </button>
        <button className="quick-add" onClick={() => add(p)}>
          <Plus size={16} /> Add to bag
        </button>
      </div>
      <div className="product-info">
        <small>{p.category}</small>
        <h3>{p.name}</h3>
        <div className="rating">
          <Star size={13} fill="currentColor" /> {p.rating} <span>·</span>{" "}
          <span className="muted">Top rated</span>
        </div>
        <div className="price-row">
          <strong>{money(p.price)}</strong>
          {p.oldPrice > p.price && <del>{money(p.oldPrice)}</del>}
          {p.oldPrice > p.price && (
            <span className="discount">
              {Math.round((1 - p.price / p.oldPrice) * 100)}% off
            </span>
          )}
        </div>
      </div>
    </article>
  );
  if (page === "vendor")
    return <VendorPortal user={user} onBack={() => setPage("home")} />;
  if (page === "admin")
    return (
      <AdminDashboard
        onBack={() => setPage("home")}
        onLogout={() => {
          localStorage.removeItem("token");
          localStorage.removeItem("ms-user");
          setUser(null);
          setPage("home");
        }}
      />
    );
  return (
    <div className="app">
      <div className="announcement">
        A little something for your everyday.{" "}
        <b>Free shipping on orders over ₹999</b>
        <span>✦</span>
      </div>
      <header className="header">
        <Link className="brand" to="/" onClick={() => setPage("home")}>
          <span className="brand-mark">M</span>market<span>sphere</span>
        </Link>
        <nav className={mobile ? "nav open" : "nav"}>
          {categories.slice(1).map((c) => (
            <button
              key={c}
              onClick={() => {
                setCategory(c);
                setPage("home");
                setMobile(false);
              }}
            >
              {c}
            </button>
          ))}
          <button
            onClick={() => {
              setCategory("All");
              setPage("home");
              setMobile(false);
            }}
          >
            Explore
          </button>
        </nav>
        <div className="header-actions">
          <label className="searchbox">
            <Search size={18} />
            <input
              placeholder="Search products..."
              value={query}
              onChange={(e) => {
                setQuery(e.target.value);
                setPage("home");
              }}
            />
            <kbd>⌘ K</kbd>
          </label>
          <button
            className="icon-btn"
            onClick={() => setPage("wishlist")}
            title="Wishlist"
          >
            <Heart size={20} />
          </button>
          <button
            className="icon-btn user-btn"
            onClick={() => setPage("auth")}
            title="Account"
          >
            <UserRound size={20} />
          </button>
          {user?.role === "ADMIN" && (
            <button className="text-link" onClick={() => setPage("admin")}>
              Admin
            </button>
          )}
          {user?.role === "VENDOR" && (
            <button className="text-link" onClick={() => setPage("vendor")}>
              Seller
            </button>
          )}
          <button className="bag-btn" onClick={() => setPage("cart")}>
            <ShoppingBag size={19} />
            <span>Bag</span>
            <b>{count}</b>
          </button>
          <button className="mobile-menu" onClick={() => setMobile(!mobile)}>
            {mobile ? <X /> : <Menu />}
          </button>
        </div>
      </header>
      {page === "home" && (
        <>
          <section className="hero">
            <div className="hero-copy">
              <span className="eyebrow">THE EVERYDAY EDIT · 2026</span>
              <h1>
                Good things.
                <br />
                <em>Better prices.</em>
              </h1>
              <p>
                Thoughtfully picked essentials, little upgrades, and things
                you'll love living with.
              </p>
              <button
                className="dark-button"
                onClick={() =>
                  document
                    .getElementById("shop")
                    .scrollIntoView({ behavior: "smooth" })
                }
              >
                Shop the collection <ArrowRight size={17} />
              </button>
              <div className="hero-note">
                <span className="avatars">● ● ●</span>
                <span>Curated by people, for people.</span>
              </div>
            </div>
            <div className="hero-art">
              <img
                src="https://images.unsplash.com/photo-1523275335684-37898b6baf30?auto=format&fit=crop&w=1000&q=85"
                alt="Minimal watch and everyday accessories"
              />
              <div className="floating-note">
                <span>THE DAILY FAVORITE</span>
                <b>
                  Small upgrades,
                  <br />
                  big difference.
                </b>
                <span className="note-arrow">↗</span>
              </div>
              <div className="hero-stamp">
                MADE FOR
                <br />
                YOUR EVERYDAY
              </div>
            </div>
          </section>
          <section className="trust-strip">
            <div>
              <Truck />
              <span>
                <b>Free shipping</b>
                <small>On orders above ₹999</small>
              </span>
            </div>
            <div>
              <ShieldCheck />
              <span>
                <b>Secure checkout</b>
                <small>Your data stays protected</small>
              </span>
            </div>
            <div>
              <RefreshCw />
              <span>
                <b>Easy returns</b>
                <small>7-day return window</small>
              </span>
            </div>
            <div>
              <Package />
              <span>
                <b>Quality picks</b>
                <small>Curated, not crowded</small>
              </span>
            </div>
          </section>
          <section className="shop-section" id="shop">
            <div className="section-heading">
              <div>
                <span className="eyebrow">A FEW GOOD THINGS</span>
                <h2>
                  Shop the edit<span>.</span>
                </h2>
              </div>
              <button
                className="text-link"
                onClick={() => {
                  setCategory("All");
                  setQuery("");
                }}
              >
                View all products <ArrowRight size={16} />
              </button>
            </div>
            <div className="category-pills">
              {categories.map((c) => (
                <button
                  className={category === c ? "selected" : ""}
                  key={c}
                  onClick={() => setCategory(c)}
                >
                  {c}
                </button>
              ))}
            </div>
            <div className="product-grid">
              {filtered.map((p) => (
                <ProductCard p={p} key={p.id} />
              ))}
            </div>
            {!filtered.length && (
              <div className="empty">
                No products match that search. Try another keyword.
              </div>
            )}
          </section>
          <section className="editorial">
            <div className="editorial-image">
              <img
                src="https://images.unsplash.com/photo-1490481651871-ab68de25d43d?auto=format&fit=crop&w=900&q=80"
                alt="Style inspiration"
              />
            </div>
            <div className="editorial-copy">
              <span className="eyebrow">A NOTE FROM US</span>
              <h2>
                Less scrolling.
                <br />
                <em>More finding.</em>
              </h2>
              <p>
                We believe shopping should feel simple. A considered collection,
                fair prices, and useful things that earn their place in your
                day.
              </p>
              <button
                className="text-link"
                onClick={() =>
                  document
                    .getElementById("shop")
                    .scrollIntoView({ behavior: "smooth" })
                }
              >
                Find your next favorite <ArrowRight size={16} />
              </button>
            </div>
          </section>
        </>
      )}
      {page === "cart" && (
        <main className="page-wrap">
          <div className="section-heading">
            <div>
              <span className="eyebrow">YOUR SELECTION</span>
              <h2>
                Your bag<span>.</span>
              </h2>
            </div>
            <button className="text-link" onClick={() => setPage("home")}>
              Continue shopping <ArrowRight />
            </button>
          </div>
          {!cart.length ? (
            <div className="empty">
              <ShoppingBag size={38} />
              <h3>Your bag is taking a break.</h3>
              <p>Let's find something you'll love.</p>
              <button className="dark-button" onClick={() => setPage("home")}>
                Explore products
              </button>
            </div>
          ) : (
            <div className="cart-layout">
              <div className="cart-items">
                {cart.map((i) => (
                  <div className="cart-item" key={i.id}>
                    <img src={i.image} alt={i.name} />
                    <div className="cart-item-info">
                      <small>{i.category}</small>
                      <h3>{i.name}</h3>
                      <b>{money(i.price)}</b>
                      <div className="qty">
                        <button onClick={() => qty(i.id, -1)}>
                          <Minus size={14} />
                        </button>
                        <span>{i.qty}</span>
                        <button onClick={() => qty(i.id, 1)}>
                          <Plus size={14} />
                        </button>
                        <button
                          className="remove"
                          onClick={() => qty(i.id, -i.qty)}
                        >
                          <Trash2 size={15} />
                        </button>
                      </div>
                    </div>
                    <strong>{money(i.price * i.qty)}</strong>
                  </div>
                ))}
              </div>
              <aside className="summary">
                <h3>Order summary</h3>
                <div>
                  <span>Subtotal</span>
                  <b>{money(total)}</b>
                </div>
                <div>
                  <span>Shipping</span>
                  <b>{total >= 999 ? "FREE" : "₹79"}</b>
                </div>
                <div className="summary-total">
                  <span>Total</span>
                  <strong>{money(total + (total >= 999 ? 0 : 79))}</strong>
                </div>
                <button
                  className="dark-button full"
                  onClick={() =>
                    user
                      ? toast("Checkout API integration is the next step")
                      : setPage("auth")
                  }
                >
                  Continue to checkout <ArrowRight size={16} />
                </button>
                <p>
                  <ShieldCheck size={14} /> Secure checkout · Easy returns
                </p>
              </aside>
            </div>
          )}
        </main>
      )}
      {page === "wishlist" && (
        <main className="page-wrap">
          <div className="section-heading">
            <div>
              <span className="eyebrow">SAVED FOR LATER</span>
              <h2>
                Your wishlist<span>.</span>
              </h2>
            </div>
          </div>
          <div className="product-grid">
            {products
              .filter((p) => wishlist.includes(p.id))
              .map((p) => (
                <ProductCard p={p} key={p.id} />
              ))}
          </div>
          {!wishlist.length && (
            <div className="empty">
              <Heart size={36} />
              <h3>Keep the good ones close.</h3>
              <p>Tap the heart on anything you like.</p>
              <button className="dark-button" onClick={() => setPage("home")}>
                Browse products
              </button>
            </div>
          )}
        </main>
      )}
      {page === "auth" && (
        <main className="auth-wrap">
          <div className="auth-art">
            <span className="eyebrow">WELCOME TO YOUR EVERYDAY</span>
            <h2>
              Find your
              <br />
              <em>kind of good.</em>
            </h2>
            <p>
              Useful things. Thoughtful prices. A little joy in every order.
            </p>
          </div>
          <form className="auth-form" onSubmit={submitAuth}>
            <span className="eyebrow">
              {authMode === "login" ? "YOUR ACCOUNT" : "JOIN THE SPHERE"}
            </span>
            <h2>{authMode === "login" ? "Welcome back" : "Create account"}</h2>
            <p>
              {authMode === "login"
                ? "Sign in to pick up where you left off."
                : "A better shopping experience starts here."}
            </p>
            {authMode === "register" && (
              <label>
                Full name
                <input
                  required
                  value={form.name}
                  onChange={(e) => setForm({ ...form, name: e.target.value })}
                  placeholder="Your name"
                />
              </label>
            )}
            <label>
              Email address
              <input
                required
                type="email"
                value={form.email}
                onChange={(e) => setForm({ ...form, email: e.target.value })}
                placeholder="you@example.com"
              />
            </label>
            <label>
              Password
              <input
                required
                type="password"
                value={form.password}
                onChange={(e) => setForm({ ...form, password: e.target.value })}
                placeholder="••••••••"
              />
            </label>
            {authMode === "register" && (
              <label>
                Phone
                <input
                  value={form.phone}
                  onChange={(e) => setForm({ ...form, phone: e.target.value })}
                  placeholder="10-digit mobile number"
                />
              </label>
            )}
            <button className="dark-button full">
              {authMode === "login" ? "Sign in" : "Create account"}{" "}
              <ArrowRight size={16} />
            </button>
            <div className="switch-auth">
              {authMode === "login"
                ? "New around here?"
                : "Already have an account?"}{" "}
              <button
                type="button"
                onClick={() =>
                  setAuthMode(authMode === "login" ? "register" : "login")
                }
              >
                {authMode === "login" ? "Create account" : "Sign in"}
              </button>
            </div>
          </form>
        </main>
      )}
      <footer>
        <div className="footer-top">
          <Link className="brand" to="/">
            <span className="brand-mark">M</span>market<span>sphere</span>
          </Link>
          <p>Good things for your everyday.</p>
          <div className="footer-links">
            <a href="#shop">Shop</a>
            <a href="#shop">About</a>
            <a href="#shop">Help & support</a>
          </div>
        </div>
        <div className="footer-bottom">
          <span>© 2026 MarketSphere. Made for everyday.</span>
          <span>Thoughtfully picked. Fairly priced.</span>
        </div>
      </footer>
      {notice && (
        <div className="toast">
          <Check size={17} />
          {notice}
        </div>
      )}
    </div>
  );
}
export default App;
