import React,{useEffect,useState} from 'react';
import api from '../api/axiosInstance';
const unwrap=r=>r?.data?.data??r?.data;
export default function VendorPortal({user,onBack}){
 const [vendor,setVendor]=useState(null),[products,setProducts]=useState([]),[status,setStatus]=useState(''),[busy,setBusy]=useState(false),[error,setError]=useState('');
 const [form,setForm]=useState({storeName:'',description:'',businessEmail:'',businessPhone:''});
 const userId=user?.id||user?.userId;
 const load=async()=>{try{let s=unwrap(await api.get(`/vendors/status/${userId}`));setStatus(typeof s==='string'?s:s?.status||'');try{let v=unwrap(await api.get(`/vendors/my-store/${userId}`));setVendor(v);if(v?.id){setProducts(unwrap(await api.get(`/products/vendor/${v.id}`))||[])}}catch{}}catch(e){setError('Vendor status could not be loaded. '+(e.response?.data?.message||''))}};
 useEffect(()=>{if(userId)load()},[userId]);
 const register=async e=>{e.preventDefault();setBusy(true);setError('');try{await api.post(`/vendors/register/${userId}`,form);await load();setError('Application submitted. Approval is required before listing products.')}catch(e){setError(e.response?.data?.message||'Vendor application failed')}finally{setBusy(false)}};
 return <div className="vendor-page"><header><h1>MarketSphere <span>Seller Studio</span></h1><button onClick={onBack}>← Storefront</button></header><main><h2>Vendor workspace</h2><p>Application status: <b>{status||'Not registered / unavailable'}</b></p>{error&&<div className="notice">{error}</div>}{!vendor&&<form className="vendor-form" onSubmit={register}><h3>Apply to become a vendor</h3>{Object.entries(form).map(([k,v])=><label key={k}>{k.replace(/([A-Z])/g,' $1')}<input required value={v} onChange={e=>setForm({...form,[k]:e.target.value})}/></label>)}<button disabled={busy}>{busy?'Submitting…':'Submit application'}</button></form>}{vendor&&<section className="vendor-card"><h3>{vendor.storeName||vendor.name||'My store'}</h3><p>{vendor.description}</p><p>Store ID: {vendor.id} · Status: {vendor.status}</p><h3>My products</h3>{products.map(p=><div className="vendor-product" key={p.id}>{p.name} — ₹{p.price} — {p.status}</div>)}<p>Product create/update APIs are available in the backend; use an approved vendor account to list products.</p></section>}</main></div>
}
