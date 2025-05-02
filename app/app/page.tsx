"use client"

import type React from "react"

import { useState } from "react"
import { useRouter } from "next/navigation"
import { useAuth } from "@/context/auth-context"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs"
import { BookOpen } from "lucide-react"

export default function LoginPage() {
  const router = useRouter()
  const { login } = useAuth()
  const [activeTab, setActiveTab] = useState("usuario")

  // Usuario login state
  const [usuarioId, setUsuarioId] = useState("")
  const [usuarioPassword, setUsuarioPassword] = useState("")
  const [usuarioError, setUsuarioError] = useState("")

  // Admin login state
  const [adminPassword, setAdminPassword] = useState("")
  const [adminError, setAdminError] = useState("")

  const handleUsuarioLogin = async (e: React.FormEvent) => {
    e.preventDefault()
    setUsuarioError("")

    if (!usuarioId || !usuarioPassword) {
      setUsuarioError("Por favor ingrese ID y contraseña")
      return
    }

    const success = await login("usuario", {
      idUsuario: usuarioId,
      password: usuarioPassword,
    })

    if (success) {
      router.push("/dashboard/usuario")
    } else {
      setUsuarioError("Credenciales inválidas")
    }
  }

  const handleAdminLogin = async (e: React.FormEvent) => {
    e.preventDefault()
    setAdminError("")

    if (!adminPassword) {
      setAdminError("Por favor ingrese la contraseña")
      return
    }

    const success = await login("administrador", {
      password: adminPassword,
    })

    if (success) {
      router.push("/dashboard/admin")
    } else {
      setAdminError("Contraseña inválida")
    }
  }

  return (
    <div className="flex items-center justify-center min-h-screen bg-gray-50">
      <Card className="w-full max-w-md">
        <CardHeader className="space-y-1 text-center">
          <div className="flex justify-center mb-2">
            <BookOpen className="h-12 w-12 text-emerald-600" />
          </div>
          <CardTitle className="text-2xl">Biblioteca UNT</CardTitle>
          <CardDescription>Seleccione su tipo de usuario para ingresar al sistema</CardDescription>
        </CardHeader>
        <CardContent>
          <Tabs defaultValue="usuario" value={activeTab} onValueChange={setActiveTab}>
            <TabsList className="grid w-full grid-cols-2">
              <TabsTrigger value="usuario">Usuario</TabsTrigger>
              <TabsTrigger value="administrador">Administrador</TabsTrigger>
            </TabsList>

            <TabsContent value="usuario">
              <form onSubmit={handleUsuarioLogin} className="space-y-4 mt-4">
                <div className="space-y-2">
                  <Label htmlFor="usuario-id">ID de Usuario</Label>
                  <Input
                    id="usuario-id"
                    value={usuarioId}
                    onChange={(e) => setUsuarioId(e.target.value)}
                    placeholder="Ingrese su ID"
                  />
                </div>
                <div className="space-y-2">
                  <Label htmlFor="usuario-password">Contraseña</Label>
                  <Input
                    id="usuario-password"
                    type="password"
                    value={usuarioPassword}
                    onChange={(e) => setUsuarioPassword(e.target.value)}
                    placeholder="Ingrese su contraseña"
                  />
                </div>
                {usuarioError && <p className="text-sm text-red-500">{usuarioError}</p>}
                <Button type="submit" className="w-full">
                  Iniciar Sesión
                </Button>
              </form>
            </TabsContent>

            <TabsContent value="administrador">
              <form onSubmit={handleAdminLogin} className="space-y-4 mt-4">
                <div className="space-y-2">
                  <Label htmlFor="admin-password">Contraseña de Administrador</Label>
                  <Input
                    id="admin-password"
                    type="password"
                    value={adminPassword}
                    onChange={(e) => setAdminPassword(e.target.value)}
                    placeholder="Ingrese la contraseña de administrador"
                  />
                </div>
                {adminError && <p className="text-sm text-red-500">{adminError}</p>}
                <Button type="submit" className="w-full">
                  Iniciar Sesión
                </Button>
              </form>
            </TabsContent>
          </Tabs>
        </CardContent>
      </Card>
    </div>
  )
}
