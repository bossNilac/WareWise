import { useEffect, useState } from "react";

export type ThemeMode = "dark" | "light";

const themeKey = "warewise.web.theme";

function readTheme(): ThemeMode {
  return localStorage.getItem(themeKey) === "light" ? "light" : "dark";
}

export function useTheme() {
  const [theme, setThemeState] = useState<ThemeMode>(readTheme);

  useEffect(() => {
    document.documentElement.dataset.theme = theme;
    localStorage.setItem(themeKey, theme);
  }, [theme]);

  const setTheme = (next: ThemeMode) => setThemeState(next);

  return { theme, setTheme } as const;
}
