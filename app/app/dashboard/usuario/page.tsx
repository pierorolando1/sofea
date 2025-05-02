"use client"

import { useEffect } from "react"
import { useRouter } from "next/navigation"
import { useAuth } from "@/context/auth-context"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { BookOpen, BookMarked } from "lucide-react"

export default function UsuarioDashboard() {
  const { userType, usuario } = useAuth()
  const router = useRouter()

  useEffect(() => {
    if (userType !== "usuario") {
      router.push("/")
    }
  }, [userType, router])

  if (userType !== "usuario") {
    return null
  }

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-3xl font-bold tracking-tight">Bienvenido, {usuario?.nombre || "Usuario"}</h1>
        <p className="text-muted-foreground">Gestione sus préstamos y explore el catálogo de libros.</p>
      </div>

      <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
        <Card className="cursor-pointer hover:bg-gray-50" onClick={() => router.push("/libros")}>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">Catálogo de Libros</CardTitle>
            <BookOpen className="h-4 w-4 text-emerald-600" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">Explorar</div>
            <p className="text-xs text-muted-foreground">Busque y explore todos los libros disponibles</p>
          </CardContent>
        </Card>

        <Card className="cursor-pointer hover:bg-gray-50" onClick={() => router.push("/prestamos")}>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">Mis Préstamos</CardTitle>
            <BookMarked className="h-4 w-4 text-emerald-600" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">Gestionar</div>
            <p className="text-xs text-muted-foreground">Vea y gestione sus préstamos actuales</p>
          </CardContent>
        </Card>
      </div>
    </div>
  )
}
