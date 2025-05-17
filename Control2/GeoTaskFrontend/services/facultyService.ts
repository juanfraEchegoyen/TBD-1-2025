import { useRuntimeConfig } from "#app";
import { type Career, type Department, type Faculty } from "~/types/types";

export default class facultiesService {
  private baseFacultyURL: string;

  constructor() {
    const config = useRuntimeConfig();
    this.baseFacultyURL = `${config.public.apiBase}/api/faculties`;
  }

  async getFaculties() {
    try {
      return await $fetch<Faculty[]>(`${this.baseFacultyURL}`, {
        params: {
          includeDepartments: "true",
        },
      });
    } catch (error) {
      console.error("Error fetching carrers:", error);
      throw error;
    }
  }

  async getDepartmentById(id: string) {
    try {
      return await $fetch<Faculty>(`${this.baseFacultyURL}/${id}`, {
        params: {
          includeDepartments: "true",
        },
      });
    } catch (error) {
      console.error(`Error fetching carrer with id ${id}:`, error);
      throw error;
    }
  }

  async getDepartmentsByFacultyId(id: string) {
    try {
      return await $fetch<Department[]>(`${this.baseFacultyURL}/${id}/departments`, {
        params: {
          includeCareers: "true",
        },
      });
    } catch (error) {
      console.error(`Error fetching carrers for department with id ${id}:`, error);
      throw error;
    }
  }

  async getCarrersByFacultyId(id: string) {
    try {
      return await $fetch<any>(`${this.baseFacultyURL}/${id}/all-careers`, {
        params: {
          includeFaculty: "true",
        },
      });
    } catch (error) {
      console.error(`Error fetching carrers for department with id ${id}:`, error);
      throw error;
    }
  }
}
