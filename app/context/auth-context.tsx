"use client"

import { createContext, useContext, useState, useEffect, type ReactNode } from "react"
import { useRouter } from "next/navigation"

type UserType = "usuario" | "administrador" | null

type Usuario = {
  id: string
  nombre: string
  direccion: string
  email: string
  telefono: string
  tipo: "EXTERNO" | "ESTUDIANTE"
}

interface AuthContextType {
  userType: UserType
  usuario: Usuario | null
  login: (type: UserType, credentials: any) => Promise<boolean>
  logout: () => void
  isAuthenticated: boolean
}

const AuthContext = createContext<AuthContextType | undefined>(undefined)

export function AuthProvider({ children }: { children: ReactNode }) {
  const [userType, setUserType] = useState<UserType>(null)
  const [usuario, setUsuario] = useState<Usuario | null>(null)
  const [isAuthenticated, setIsAuthenticated] = useState(false)
  const router = useRouter()

  // Check if user is authenticated on page load
  useEffect(() => {
    const storedUserType = localStorage.getItem("userType")
    const storedUsuario = localStorage.getItem("usuario")

    if (storedUserType) {
      setUserType(storedUserType as UserType)
      setIsAuthenticated(true)

      if (storedUsuario) {
        setUsuario(JSON.parse(storedUsuario))
      }
    }
  }, [])

  const login = async (type: UserType, credentials: any): Promise<boolean> => {
    try {
      let endpoint = ""

      if (type === "usuario") {
        endpoint = "http://localhost:8080/api/auth/usuario"
      } else if (type === "administrador") {
        endpoint = "http://localhost:8080/api/auth/admin"
      }

      const response = await fetch(endpoint, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(credentials),
      })

      if (!response.ok) {
        return false
      }

      // If user login, fetch user details
      if (type === "usuario" && credentials.idUsuario) {
        const userResponse = await fetch(`http://localhost:8080/api/usuarios/${credentials.idUsuario}`)
        if (userResponse.ok) {
          const userData = await userResponse.json()
          setUsuario(userData)
          localStorage.setItem("usuario", JSON.stringify(userData))
        }
      }

      setUserType(type)
      setIsAuthenticated(true)
      localStorage.setItem("userType", type)

      return true
    } catch (error) {
      console.error("Error during login:", error)
      return false
    }
  }

  const logout = () => {
    setUserType(null)
    setUsuario(null)
    setIsAuthenticated(false)
    localStorage.removeItem("userType")
    localStorage.removeItem("usuario")
    router.push("/")
  }

  return (
    <AuthContext.Provider value={{ userType, usuario, login, logout, isAuthenticated }}>
      {children}
    </AuthContext.Provider>
  )
}

export function useAuth() {
  const context = useContext(AuthContext)
  if (context === undefined) {
    throw new Error("useAuth must be used within an AuthProvider")
  }
  return context
}
