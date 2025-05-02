"use client"

import type React from "react"

import { useState, useEffect } from "react"
import { useAuth } from "@/context/auth-context"
import { Button } from "@/components/ui/button"
import { Card, CardContent } from "@/components/ui/card"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table"
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogTrigger } from "@/components/ui/dialog"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { Plus, Search, Check } from "lucide-react"
import { format } from "date-fns"
import { es } from "date-fns/locale"

interface Usuario {
  id: string
  nombre: string
  direccion: string
  email: string
  telefono: string
  tipo: "EXTERNO" | "ESTUDIANTE"
}

interface Libro {
  codigo: string
  isbn: string
  titulo: string
  autor: string
  anioPublicacion: number
  ejemplaresDisponibles: number
}

interface Prestamo {
  idPrestamo: string
  fechaPrestamo: string
  fechaDevolucion: string
  fechaRetornoReal: string | null
  usuario: Usuario
  libro: Libro
}

export default function PrestamosPage() {
  const { userType, usuario } = useAuth()
  const [prestamos, setPrestamos] = useState<Prestamo[]>([])
  const [loading, setLoading] = useState(true)
  const [searchTerm, setSearchTerm] = useState("")
  const [openDialog, setOpenDialog] = useState(false)

  // Form state for new loan
  const [selectedUsuarioId, setSelectedUsuarioId] = useState("")
  const [selectedIsbn, setSelectedIsbn] = useState("")

  // Lists for dropdowns
  const [usuarios, setUsuarios] = useState<Usuario[]>([])
  const [libros, setLibros] = useState<Libro[]>([])

  const isAdmin = userType === "administrador"

  useEffect(() => {
    fetchPrestamos()

    if (isAdmin) {
      fetchUsuarios()
    }

    fetchLibros()
  }, [isAdmin])

  const fetchPrestamos = async () => {
    try {
      setLoading(true)
      const url = "http://localhost:8080/api/prestamos"

      // If user is not admin, filter by user ID
      if (!isAdmin && usuario) {
        // This is a simplified approach - ideally the API would support filtering
        // For now, we'll fetch all and filter client-side
        const response = await fetch(url)
        if (response.ok) {
          const data = await response.json()
          // Filter prestamos for current user
          const filteredData = data.filter((p: Prestamo) => p.usuario.id === usuario.id)
          setPrestamos(filteredData)
        }
      } else {
        const response = await fetch(url)
        if (response.ok) {
          const data = await response.json()
          setPrestamos(data)
        }
      }
    } catch (error) {
      console.error("Error fetching prestamos:", error)
    } finally {
      setLoading(false)
    }
  }

  const fetchUsuarios = async () => {
    try {
      const response = await fetch("http://localhost:8080/api/usuarios")
      if (response.ok) {
        const data = await response.json()
        setUsuarios(data)
      }
    } catch (error) {
      console.error("Error fetching usuarios:", error)
    }
  }

  const fetchLibros = async () => {
    try {
      const response = await fetch("http://localhost:8080/api/libros")
      if (response.ok) {
        const data = await response.json()
        setLibros(data)
      }
    } catch (error) {
      console.error("Error fetching libros:", error)
    }
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()

    try {
      const response = await fetch("http://localhost:8080/api/prestamos", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          idUsuario: isAdmin ? selectedUsuarioId : usuario?.id,
          isbn: selectedIsbn,
        }),
      })

      if (response.ok) {
        // Reset form
        setSelectedUsuarioId("")
        setSelectedIsbn("")

        // Close dialog and refresh data
        setOpenDialog(false)
        fetchPrestamos()
      }
    } catch (error) {
      console.error("Error creating prestamo:", error)
    }
  }

  const handleDevolver = async (idPrestamo: string) => {
    try {
      const response = await fetch(`http://localhost:8080/api/prestamos/${idPrestamo}/devolver`, {
        method: "PUT",
      })

      if (response.ok) {
        fetchPrestamos()
      }
    } catch (error) {
      console.error("Error devolviendo prestamo:", error)
    }
  }

  const filteredPrestamos = prestamos.filter(
    (prestamo) =>
      prestamo.libro.titulo.toLowerCase().includes(searchTerm.toLowerCase()) ||
      prestamo.usuario.nombre.toLowerCase().includes(searchTerm.toLowerCase()) ||
      prestamo.libro.isbn.toLowerCase().includes(searchTerm.toLowerCase()),
  )

  const formatDate = (dateString: string | null) => {
    if (!dateString) return "N/A"
    return format(new Date(dateString), "dd/MM/yyyy", { locale: es })
  }

  return (
    <div className="space-y-6" style={{padding: "3rem"}} >
      <div className="flex justify-between items-center">
        <div>
          <h1 className="text-3xl font-bold tracking-tight">Préstamos</h1>
          <p className="text-muted-foreground">
            {isAdmin ? "Gestione los préstamos del sistema." : "Gestione sus préstamos de libros."}
          </p>
        </div>

        <Dialog open={openDialog} onOpenChange={setOpenDialog}>
          <DialogTrigger asChild>
            <Button>
              <Plus className="h-4 w-4 mr-2" />
              Nuevo Préstamo
            </Button>
          </DialogTrigger>
          <DialogContent>
            <DialogHeader>
              <DialogTitle>Realizar Nuevo Préstamo</DialogTitle>
            </DialogHeader>
            <form onSubmit={handleSubmit} className="space-y-4">
              {isAdmin && (
                <div className="space-y-2">
                  <Label htmlFor="usuario">Usuario</Label>
                  <Select value={selectedUsuarioId} onValueChange={setSelectedUsuarioId}>
                    <SelectTrigger>
                      <SelectValue placeholder="Seleccione un usuario" />
                    </SelectTrigger>
                    <SelectContent>
                      {usuarios.map((u) => (
                        <SelectItem key={u.id} value={u.id}>
                          {u.nombre} ({u.id})
                        </SelectItem>
                      ))}
                    </SelectContent>
                  </Select>
                </div>
              )}
              <div className="space-y-2">
                <Label htmlFor="libro">Libro</Label>
                <Select value={selectedIsbn} onValueChange={setSelectedIsbn}>
                  <SelectTrigger>
                    <SelectValue placeholder="Seleccione un libro" />
                  </SelectTrigger>
                  <SelectContent>
                    {libros
                      .filter((l) => l.ejemplaresDisponibles > 0)
                      .map((l) => (
                        <SelectItem key={l.isbn} value={l.isbn}>
                          {l.titulo} ({l.isbn})
                        </SelectItem>
                      ))}
                  </SelectContent>
                </Select>
              </div>
              <Button type="submit" className="w-full" disabled={(isAdmin && !selectedUsuarioId) || !selectedIsbn}>
                Realizar Préstamo
              </Button>
            </form>
          </DialogContent>
        </Dialog>
      </div>

      <div className="relative">
        <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 h-4 w-4 text-muted-foreground" />
        <Input
          placeholder="Buscar por título, usuario o ISBN..."
          className="pl-10"
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
        />
      </div>

      <Card>
        <CardContent className="p-0">
          <Table>
            <TableHeader>
              <TableRow>
                <TableHead>ID</TableHead>
                {isAdmin && <TableHead>Usuario</TableHead>}
                <TableHead>Libro</TableHead>
                <TableHead>Fecha Préstamo</TableHead>
                <TableHead>Fecha Devolución</TableHead>
                <TableHead>Estado</TableHead>
                <TableHead>Acciones</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {loading ? (
                <TableRow>
                  <TableCell colSpan={isAdmin ? 7 : 6} className="text-center py-4">
                    Cargando...
                  </TableCell>
                </TableRow>
              ) : filteredPrestamos.length === 0 ? (
                <TableRow>
                  <TableCell colSpan={isAdmin ? 7 : 6} className="text-center py-4">
                    No se encontraron préstamos
                  </TableCell>
                </TableRow>
              ) : (
                filteredPrestamos.map((prestamo) => (
                  <TableRow key={prestamo.idPrestamo}>
                    <TableCell>{prestamo.idPrestamo.substring(0, 8)}...</TableCell>
                    {isAdmin && <TableCell>{prestamo.usuario.nombre}</TableCell>}
                    <TableCell>{prestamo.libro.titulo}</TableCell>
                    <TableCell>{formatDate(prestamo.fechaPrestamo)}</TableCell>
                    <TableCell>{formatDate(prestamo.fechaDevolucion)}</TableCell>
                    <TableCell>
                      {prestamo.fechaRetornoReal ? (
                        <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-green-100 text-green-800">
                          Devuelto
                        </span>
                      ) : (
                        <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-blue-100 text-blue-800">
                          Prestado
                        </span>
                      )}
                    </TableCell>
                    <TableCell>
                      {!prestamo.fechaRetornoReal && (
                        <Button variant="outline" size="sm" onClick={() => handleDevolver(prestamo.idPrestamo)}>
                          <Check className="h-4 w-4 mr-1" />
                          Devolver
                        </Button>
                      )}
                    </TableCell>
                  </TableRow>
                ))
              )}
            </TableBody>
          </Table>
        </CardContent>
      </Card>
    </div>
  )
}
