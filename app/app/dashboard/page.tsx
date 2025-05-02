"use client"

import { useEffect } from "react"
import { useRouter } from "next/navigation"
import { useAuth } from "@/context/auth-context"

export default function Dashboard() {
  const { userType } = useAuth()
  const router = useRouter()

  useEffect(() => {
    if (userType === "usuario") {
      router.push("/dashboard/usuario")
    } else if (userType === "administrador") {
      router.push("/dashboard/admin")
    } else {
      router.push("/")
    }
  }, [userType, router])

  return null
}
