// app/PublicLayout.tsx

import { ReactNode } from 'react';

interface LayoutProps {
  children: ReactNode;
}

export default function PublicLayout({ children }: LayoutProps) {
  return (
    <div style={{ maxWidth: '400px', margin: '0 auto', padding: '1em' }}>
      <header>
        <h1>Bienvenido</h1>
      </header>
      <main>{children}</main>
    </div>
  );
}
