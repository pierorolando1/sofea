"use client"

import Link from "next/link"
import { usePathname } from "next/navigation"
import { useAuth } from "@/context/auth-context"
import { cn } from "@/lib/utils"
import { BookOpen, Users, BookMarked, LogOut } from "lucide-react"
import { Button } from "@/components/ui/button"

export function Navigation() {
  const pathname = usePathname()
  const { userType, logout } = useAuth()

  const isAdmin = userType === "administrador"

  const links = [
    {
      name: "Libros",
      href: "/libros",
      icon: BookOpen,
      active: pathname.startsWith("/libros"),
      visible: true,
    },
    {
      name: "Préstamos",
      href: "/prestamos",
      icon: BookMarked,
      active: pathname.startsWith("/prestamos"),
      visible: true,
    },
    {
      name: "Usuarios",
      href: "/usuarios",
      icon: Users,
      active: pathname.startsWith("/usuarios"),
      visible: isAdmin,
    },
  ]

  return (
    <nav className="flex flex-col h-full bg-emerald-800 text-white w-64 p-4">
      <div className="flex items-center gap-2 mb-8 px-2">
        <BookOpen className="h-8 w-8" />
        <h1 className="text-xl font-bold">Biblioteca UNT</h1>
      </div>

      <div className="space-y-1">
        {links
          .filter((link) => link.visible)
          .map((link) => (
            <Link
              key={link.href}
              href={link.href}
              className={cn(
                "flex items-center gap-3 px-3 py-2 rounded-md transition-colors",
                link.active ? "bg-emerald-700 text-white" : "text-emerald-100 hover:bg-emerald-700 hover:text-white",
              )}
            >
              <link.icon className="h-5 w-5" />
              {link.name}
            </Link>
          ))}
      </div>

      <div className="mt-auto">
        <Button
          variant="ghost"
          className="w-full justify-start text-emerald-100 hover:bg-emerald-700 hover:text-white"
          onClick={logout}
        >
          <LogOut className="h-5 w-5 mr-2" />
          Cerrar Sesión
        </Button>
      </div>
    </nav>
  )
}
