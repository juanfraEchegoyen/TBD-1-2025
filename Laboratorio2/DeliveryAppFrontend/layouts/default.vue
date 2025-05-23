<template>
  <div class="w-full min-h-dvh overflow-auto grid grid-cols-[auto_1fr]">
    <Sidebar />
    <!-- Revisar si dejar con estas props o con overflow-auto -->
    <div class="overflow-hidden bg-gray-200 grid max-h-screen p-5">
      <slot />
    </div>
  </div>
</template>

<script lang="ts" setup>
import { Icon } from '@iconify/vue/dist/iconify.js';
</script>

<style>

</style>