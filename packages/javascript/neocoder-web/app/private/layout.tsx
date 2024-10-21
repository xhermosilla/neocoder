// app/private/layout.tsx

import { ReactNode } from 'react';

interface LayoutProps {
  children: ReactNode;
}

export default function PrivateLayout({ children }: LayoutProps) {
  return (
    <div className="min-h-screen bg-gray-100">
        <header>
            <h1>Bienvenido</h1>
        </header>
        <main>{children}</main>
    </div>
  );
}
