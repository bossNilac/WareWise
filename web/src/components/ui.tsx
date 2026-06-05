import { AlertTriangle, Bell, Check, Search, X } from "lucide-react";
import type { LucideIcon } from "lucide-react";
import type { ReactNode } from "react";
import type { Notice } from "../lib/notifications";

export function DataTable<T>({ rows, columns }: { rows: T[]; columns: Array<[string, (row: T) => ReactNode]> }) {
  return (
    <div className="table-wrap">
      <table>
        <thead>
          <tr>
            {columns.map(([title], index) => <th key={`${title}-${index}`}>{title}</th>)}
          </tr>
        </thead>
        <tbody>
          {rows.length === 0 ? (
            <tr><td className="empty-cell" colSpan={columns.length}>No records</td></tr>
          ) : (
            rows.map((row, rowIndex) => (
              <tr key={rowIndex}>
                {columns.map(([title, render], columnIndex) => <td key={`${title}-${columnIndex}`}>{render(row)}</td>)}
              </tr>
            ))
          )}
        </tbody>
      </table>
    </div>
  );
}

export function Panel({ title, children, wide = false }: { title: string; children: ReactNode; wide?: boolean }) {
  return <section className={`panel ${wide ? "wide" : ""}`}><h2>{title}</h2>{children}</section>;
}

export function KpiGrid({ items }: { items: Array<[string, string]> }) {
  return (
    <div className="kpi-grid">
      {items.map(([title, value]) => <div className="kpi-card" key={title}><span>{title}</span><strong>{value}</strong></div>)}
    </div>
  );
}

export function FilterBar({ children }: { children: ReactNode }) {
  return <div className="filter-bar"><Search size={16} />{children}</div>;
}

export function BarList({ rows }: { rows: Array<{ label: string; value: number }> }) {
  const max = Math.max(1, ...rows.map((row) => row.value));
  return (
    <div className="bar-list">
      {rows.length === 0 ? <div className="muted">No chart data</div> : null}
      {rows.map((row) => (
        <div className="bar-row" key={row.label}>
          <span>{row.label}</span><div><i style={{ width: `${Math.max(4, (row.value / max) * 100)}%` }} /></div><strong>{row.value}</strong>
        </div>
      ))}
    </div>
  );
}

export function List({ items, empty }: { items: string[]; empty: string }) {
  return <div className="list">{items.length ? items.map((item, index) => <div key={index}>{item}</div>) : <div>{empty}</div>}</div>;
}

export function TextInput({
  label,
  value,
  onChange,
  type = "text",
  required = false,
}: {
  label: string;
  value: string;
  onChange: (value: string) => void;
  type?: string;
  required?: boolean;
}) {
  return <label>{label}<input required={required} min={type === "number" ? 0 : undefined} type={type} value={value} onChange={(event) => onChange(event.target.value)} /></label>;
}

export function CheckField({ label, checked, onChange }: { label: string; checked: boolean; onChange: (checked: boolean) => void }) {
  return <label className="check-field"><input type="checkbox" checked={checked} onChange={(event) => onChange(event.target.checked)} /><span>{label}</span></label>;
}

export function IconAction({ title, icon: Icon, onClick }: { title: string; icon: LucideIcon; onClick: () => void }) {
  return <button className="icon-button" title={title} onClick={onClick}><Icon size={16} /></button>;
}

export function Toast({ notice, onClose }: { notice: Notice; onClose: () => void }) {
  if (!notice) return null;
  const Icon = notice.tone === "success" ? Check : notice.tone === "warning" ? AlertTriangle : notice.tone === "error" ? X : Bell;
  return <div className={`toast ${notice.tone}`}><Icon size={18} /><span>{notice.text}</span><button onClick={onClose}><X size={16} /></button></div>;
}
