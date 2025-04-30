<template>
  <button
    class="bg-green-700 text-white px-6 py-2 rounded-lg hover:bg-green-800 transition-colors"
    v-bind="$attrs"
    v-on="$listeners"
  >
    <slot />
  </button>
</template>