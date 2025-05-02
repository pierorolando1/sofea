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
import { Plus, Search } from "lucide-react"

interface Libro {
  codigo: string
  isbn: string
  titulo: string
  autor: string
  anioPublicacion: number
  ejemplaresDisponibles: number
}

export default function LibrosPage() {
  const { userType } = useAuth()
  const [libros, setLibros] = useState<Libro[]>([])
  const [loading, setLoading] = useState(true)
  const [searchTerm, setSearchTerm] = useState("")
  const [openDialog, setOpenDialog] = useState(false)

  // Form state
  const [isbn, setIsbn] = useState("")
  const [titulo, setTitulo] = useState("")
  const [autor, setAutor] = useState("")
  const [anioPublicacion, setAnioPublicacion] = useState("")
  const [ejemplaresDisponibles, setEjemplaresDisponibles] = useState("")

  const isAdmin = userType === "administrador"

  useEffect(() => {
    fetchLibros()
  }, [])

  const fetchLibros = async () => {
    try {
      setLoading(true)
      const response = await fetch("http://localhost:8080/api/libros")
      if (response.ok) {
        const data = await response.json()
        setLibros(data)
      }
    } catch (error) {
      console.error("Error fetching libros:", error)
    } finally {
      setLoading(false)
    }
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()

    try {
      const response = await fetch("http://localhost:8080/api/libros", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          isbn,
          titulo,
          autor,
          anioPublicacion: Number.parseInt(anioPublicacion),
          ejemplaresDisponibles: Number.parseInt(ejemplaresDisponibles),
        }),
      })

      if (response.ok) {
        // Reset form
        setIsbn("")
        setTitulo("")
        setAutor("")
        setAnioPublicacion("")
        setEjemplaresDisponibles("")

        // Close dialog and refresh data
        setOpenDialog(false)
        fetchLibros()
      }
    } catch (error) {
      console.error("Error creating libro:", error)
    }
  }

  const filteredLibros = libros.filter(
    (libro) =>
      libro.titulo.toLowerCase().includes(searchTerm.toLowerCase()) ||
      libro.autor.toLowerCase().includes(searchTerm.toLowerCase()) ||
      libro.isbn.toLowerCase().includes(searchTerm.toLowerCase()),
  )

  return (
    <div className="space-y-6 " style={{padding: "3rem"}} >
      <div className="flex justify-between items-center">
        <div>
          <h1 className="text-3xl font-bold tracking-tight">Libros</h1>
          <p className="text-muted-foreground">Explore el catálogo de libros disponibles.</p>
        </div>

        {isAdmin && (
          <Dialog open={openDialog} onOpenChange={setOpenDialog}>
            <DialogTrigger asChild>
              <Button>
                <Plus className="h-4 w-4 mr-2" />
                Nuevo Libro
              </Button>
            </DialogTrigger>
            <DialogContent>
              <DialogHeader>
                <DialogTitle>Agregar Nuevo Libro</DialogTitle>
              </DialogHeader>
              <form onSubmit={handleSubmit} className="space-y-4">
                <div className="space-y-2">
                  <Label htmlFor="isbn">ISBN</Label>
                  <Input id="isbn" value={isbn} onChange={(e) => setIsbn(e.target.value)} required />
                </div>
                <div className="space-y-2">
                  <Label htmlFor="titulo">Título</Label>
                  <Input id="titulo" value={titulo} onChange={(e) => setTitulo(e.target.value)} required />
                </div>
                <div className="space-y-2">
                  <Label htmlFor="autor">Autor</Label>
                  <Input id="autor" value={autor} onChange={(e) => setAutor(e.target.value)} required />
                </div>
                <div className="space-y-2">
                  <Label htmlFor="anioPublicacion">Año de Publicación</Label>
                  <Input
                    id="anioPublicacion"
                    type="number"
                    value={anioPublicacion}
                    onChange={(e) => setAnioPublicacion(e.target.value)}
                    required
                  />
                </div>
                <div className="space-y-2">
                  <Label htmlFor="ejemplaresDisponibles">Ejemplares Disponibles</Label>
                  <Input
                    id="ejemplaresDisponibles"
                    type="number"
                    value={ejemplaresDisponibles}
                    onChange={(e) => setEjemplaresDisponibles(e.target.value)}
                    required
                  />
                </div>
                <Button type="submit" className="w-full">
                  Guardar
                </Button>
              </form>
            </DialogContent>
          </Dialog>
        )}
      </div>

      <div className="relative">
        <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 h-4 w-4 text-muted-foreground" />
        <Input
          placeholder="Buscar por título, autor o ISBN..."
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
                <TableHead>ISBN</TableHead>
                <TableHead>Título</TableHead>
                <TableHead>Autor</TableHead>
                <TableHead>Año</TableHead>
                <TableHead>Disponibles</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {loading ? (
                <TableRow>
                  <TableCell colSpan={5} className="text-center py-4">
                    Cargando...
                  </TableCell>
                </TableRow>
              ) : filteredLibros.length === 0 ? (
                <TableRow>
                  <TableCell colSpan={5} className="text-center py-4">
                    No se encontraron libros
                  </TableCell>
                </TableRow>
              ) : (
                filteredLibros.map((libro) => (
                  <TableRow key={libro.isbn}>
                    <TableCell>{libro.isbn}</TableCell>
                    <TableCell>{libro.titulo}</TableCell>
                    <TableCell>{libro.autor}</TableCell>
                    <TableCell>{libro.anioPublicacion}</TableCell>
                    <TableCell>{libro.ejemplaresDisponibles}</TableCell>
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
