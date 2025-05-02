"use client"

import { useEffect } from "react"
import { useRouter } from "next/navigation"
import { useAuth } from "@/context/auth-context"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { BookOpen, Users, BookMarked } from "lucide-react"

export default function AdminDashboard() {
  const { userType } = useAuth()
  const router = useRouter()

  useEffect(() => {
    if (userType !== "administrador") {
      router.push("/")
    }
  }, [userType, router])

  if (userType !== "administrador") {
    return null
  }

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-3xl font-bold tracking-tight">Panel de Administración</h1>
        <p className="text-muted-foreground">Gestione libros, usuarios y préstamos del sistema.</p>
      </div>

      <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
        <Card className="cursor-pointer hover:bg-gray-50" onClick={() => router.push("/libros")}>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">Libros</CardTitle>
            <BookOpen className="h-4 w-4 text-emerald-600" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">Gestionar</div>
            <p className="text-xs text-muted-foreground">Administre el catálogo de libros</p>
          </CardContent>
        </Card>

        <Card className="cursor-pointer hover:bg-gray-50" onClick={() => router.push("/usuarios")}>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">Usuarios</CardTitle>
            <Users className="h-4 w-4 text-emerald-600" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">Gestionar</div>
            <p className="text-xs text-muted-foreground">Administre los usuarios del sistema</p>
          </CardContent>
        </Card>

        <Card className="cursor-pointer hover:bg-gray-50" onClick={() => router.push("/prestamos")}>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">Préstamos</CardTitle>
            <BookMarked className="h-4 w-4 text-emerald-600" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">Gestionar</div>
            <p className="text-xs text-muted-foreground">Administre los préstamos del sistema</p>
          </CardContent>
        </Card>
      </div>
    </div>
  )
}
