import type { ReactNode } from "react"
import { Navigation } from "@/components/navigation"

export default function DashboardLayout({ children }: { children: ReactNode }) {
  return (
    <div className="flex h-screen bg-gray-100">
      <Navigation />
      <main className="flex-1 p-6 overflow-auto">{children}</main>
    </div>
  )
}
