import DashboardFrame from "@/components/dashboard/DashboardFrame";

type ModulePageProps = {
  params: Promise<{ module: string }>;
};

export default async function ModulePage({ params }: ModulePageProps) {
  await params;
  return <DashboardFrame />;
}
